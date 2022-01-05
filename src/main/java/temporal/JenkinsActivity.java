package temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.json.JSONObject;

@ActivityInterface
public interface JenkinsActivity {

    @ActivityMethod
    String triggerJenkinsBuild(String jobId);
}
