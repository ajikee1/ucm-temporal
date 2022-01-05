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
            Promise<String> locationUrlPromise = Async.function(jenkinsActivity::triggerJenkinsBuild, job);
            String locationUrl = locationUrlPromise.get();
            System.out.println("***** LOCATION URL : " + locationUrl + " *****");

            Promise<String> executionUrlPromise = Async.function(jenkinsActivity::executableUrlFromLocationUrl, locationUrl, job);
            String executionUrl = executionUrlPromise.get();
            System.out.println("***** EXECUTION URL : " + executionUrl + " *****");

            buildStatusPromiseList.add(Async.function(jenkinsActivity::getBuildStatus, executionUrl, job));

            /*
                Promise<String> buildStatusPromise =Async.function(jenkinsActivity::getBuildStatus, executionUrl);
                String buildStatus = buildStatusPromise.get();
                System.out.println("***** BUILD STATUS : " + buildStatus + " *****");
             */
        }

        /* Wait for all Jenkins jobs to return a build status */
        Promise.allOf(buildStatusPromiseList).get();

        for (Promise<String> buildStatusPromise : buildStatusPromiseList) {
            String buildStatus = buildStatusPromise.get();
            System.out.println("***** BUILD STATUS : " + buildStatus + " *****");
        }


    }
}
