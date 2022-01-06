package temporal.Jenkins;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

@ActivityInterface
public interface JenkinsActivity {

    @ActivityMethod
    Map<String, String> triggerJenkinsBuild(String jobId);
}
