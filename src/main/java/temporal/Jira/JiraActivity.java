package temporal.Jira;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import temporal.Dao.buildResultsDAO;

import java.util.List;
import java.util.Map;

@ActivityInterface
public interface JiraActivity {

    @ActivityMethod
    String getIssueStatus(String issueId);

    void addResultsToJira(List<buildResultsDAO> buildResults );


}
