package temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInfo;
import temporal.Dao.buildResultsDAO;
import temporal.Jenkins.JenkinsActivity;
import temporal.Jira.JiraActivity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UcmWorkFlowImp implements UcmWorkFlow {

    boolean ticketApproved = false;

    private final JenkinsActivity jenkinsActivity = Workflow.newActivityStub(JenkinsActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    private final JiraActivity jiraActivity = Workflow.newActivityStub(JiraActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    @Override
    public void initiateWorkFlow(List<String> jobList, String issueId) {

        WorkflowInfo we = Workflow.getInfo();
        String wfRunId = we.getRunId();
        String workFlowId = we.getWorkflowId();

        List<buildResultsDAO> buildResults = new ArrayList<>();

        while (!ticketApproved) {
            Promise<String> ticketStatusPromise = Async.function(jiraActivity::getIssueStatus, issueId);
            String ticketStatus = ticketStatusPromise.get();
            System.out.println("Current Issue Status: " + ticketStatus);

            Workflow.sleep(Duration.ofSeconds(2));

            if (ticketStatus.equalsIgnoreCase("Approved")) {
                ticketApproved = true;
            }
        }

        Workflow.await(() -> ticketApproved);

        List<Promise<Map<String, String>>> buildStatusPromiseList = new ArrayList<>();

        for (String job : jobList) {
            buildStatusPromiseList.add(Async.function(jenkinsActivity::triggerJenkinsBuild, job));
        }

        Promise.allOf(buildStatusPromiseList).get();

        for (Promise<Map<String, String>> buildStatusPromise : buildStatusPromiseList) {
            String jobId = null; String buildStatus = null;
            Map<String, String> buildStatuses = buildStatusPromise.get();
            for (Map.Entry m : buildStatuses.entrySet()) {
                 jobId = m.getKey().toString();
                 buildStatus = m.getValue().toString();
            }
            buildResults.add(new buildResultsDAO( workFlowId, wfRunId, jobId, buildStatus));
        }

        for(buildResultsDAO buildResult: buildResults){
            System.out.println("WORKFLOW ID: " + buildResult.getWorkflowId() + " RUN ID: " + buildResult.getRunId() + " JENKINS JOB ID: " + buildResult.getJobId() + " BUILD STATUS: " + buildResult.getBuildStatus());
        }


    }

}
