package temporal.Jenkins;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface JenkinsActivity {

    @ActivityMethod
    String triggerJenkinsBuild(String jobId);
}
