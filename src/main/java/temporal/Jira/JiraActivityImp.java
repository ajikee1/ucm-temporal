package temporal.Jira;

import helper.ApiHelper;
import io.temporal.activity.Activity;
import org.apache.http.HttpHeaders;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Properties;

public class JiraActivityImp implements JiraActivity {



    @Override
    public String pollJiraTicket(String issueId) {

        return "";
    }

    public String getIssueStatus(String issueId){

        ApiHelper apiHelper = new ApiHelper();

        Properties jiraProps = apiHelper.loadProperties("jira.properties");
        String jiraDomain = jiraProps.getProperty("jira_domain");
        String authHeader = apiHelper.generateEncodedAuthHeaderJira();

        String ticketStatus = null;
        String url = jiraDomain + "/rest/api/2/issue/" + issueId + "?fields=status";

        HashMap<String, String> headersHm = new HashMap<>();
        headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);

        String httpResponse = apiHelper.runHttpGetRequest(url, headersHm);

        if (httpResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(httpResponse.toString());
                ticketStatus = jsonObject.getJSONObject("fields").getJSONObject("status").get("name").toString();

            } catch (JSONException e) {
                Activity.wrap(e);
            }

        }

        return ticketStatus;
    }
}
