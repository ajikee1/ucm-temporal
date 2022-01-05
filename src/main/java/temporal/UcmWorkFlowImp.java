package temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UcmWorkFlowImp implements UcmWorkFlow {

    private final JenkinsActivity jenkinsActivity = Workflow.newActivityStub(JenkinsActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    @Override
    public void initiateWorkFlow(List<String> jobList) {
        List<Promise<String>> buildStatusPromiseList = new ArrayList<>();

        for (String job : jobList) {
            buildStatusPromiseList.add(Async.function(jenkinsActivity::triggerJenkinsBuild, job));
        }

        /* Wait for all Jenkins jobs to return a build status */
        Promise.allOf(buildStatusPromiseList).get();

        for (Promise<String> buildStatusPromise : buildStatusPromiseList) {
            String buildStatus = buildStatusPromise.get();
            System.out.println("***** BUILD STATUS : " + buildStatus + " *****");
        }

    }
}
