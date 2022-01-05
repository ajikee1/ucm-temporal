package temporal.Jira;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface JiraActivity {

    @ActivityMethod
    String pollJiraTicket(String issueId);

    @ActivityMethod
    String getIssueStatus(String issueId);
}
