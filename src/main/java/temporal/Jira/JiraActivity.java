package temporal.Jira;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import temporal.Dao.buildResultsDAO;

import java.util.List;

@ActivityInterface
public interface JiraActivity {

    @ActivityMethod
    String getIssueStatus(String issueId);

    @ActivityMethod
    void writeResultsToExcel(List<buildResultsDAO> l);

}
