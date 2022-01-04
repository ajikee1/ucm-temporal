package temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class UcmWorkFlowImp implements UcmWorkFlow{

    private final JenkinsActivity jenkinsActivity = Workflow.newActivityStub(JenkinsActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(200)).build());

    @Override
    public void initiateWorkFlow() {

       Promise<String> locationUrlPromise = Async.function(jenkinsActivity::triggerJenkinsBuild, "temporal_demo");
       String locationUrl = locationUrlPromise.get();
       System.out.println("locationUrl: " + locationUrl);

        Promise<String> executionUrlPromise =Async.function(jenkinsActivity:: executableUrlFromLocationUrl,locationUrl);
        String executionUrl = executionUrlPromise.get();
        System.out.println("executionUrl: " + executionUrl);

    }
}
