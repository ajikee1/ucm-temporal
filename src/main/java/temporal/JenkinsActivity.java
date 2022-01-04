package temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.json.JSONObject;

@ActivityInterface
public interface JenkinsActivity {

    @ActivityMethod
    String triggerJenkinsBuild(String jobId);

    @ActivityMethod
    String executableUrlFromLocationUrl(String locationUrl);

    @ActivityMethod
    String getBuildStatus(String executionUrl);
}
