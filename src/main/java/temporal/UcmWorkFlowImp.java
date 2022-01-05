package temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import temporal.Jenkins.JenkinsActivity;
import temporal.Jira.JiraActivity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UcmWorkFlowImp implements UcmWorkFlow {

    boolean ticketApproved = false;

    private final JenkinsActivity jenkinsActivity = Workflow.newActivityStub(JenkinsActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    private final JiraActivity jiraActivity = Workflow.newActivityStub(JiraActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    @Override
    public void initiateWorkFlow(List<String> jobList, String issueId) {

        while(!ticketApproved){
            Promise<String> ticketStatusPromise = Async.function(jiraActivity::getIssueStatus, issueId);
            String ticketStatus = ticketStatusPromise.get();
            System.out.println("Current Issue Status: " + ticketStatus);

            Workflow.sleep(Duration.ofSeconds(2));

            if(ticketStatus.equalsIgnoreCase("Approved")){
                ticketApproved = true;
            }
        }

        Workflow.await(() -> ticketApproved);

        List<Promise<String>> buildStatusPromiseList = new ArrayList<>();

        for (String job : jobList) {
            buildStatusPromiseList.add(Async.function(jenkinsActivity::triggerJenkinsBuild, job));
        }

        Promise.allOf(buildStatusPromiseList).get();

        for (Promise<String> buildStatusPromise : buildStatusPromiseList) {
            String buildStatus = buildStatusPromise.get();
            System.out.println("***** BUILD STATUS : " + buildStatus + " *****");
        }


    }
}
