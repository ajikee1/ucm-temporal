package temporal.Jira;

import com.google.gson.Gson;
import helper.ApiHelper;
import io.temporal.activity.Activity;
import org.apache.http.HttpHeaders;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;
import temporal.Dao.buildResultsDAO;

import java.util.*;

public class JiraActivityImp implements JiraActivity {

    public String getIssueStatus(String issueId) {

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

    @Override
    public void addResultsToJira(List<buildResultsDAO> buildResults) {

        try {
            JSONObject json = new JSONObject();

            for (buildResultsDAO buildResult : buildResults) {
                String t = new Gson().toJson(buildResult);
                JSONObject js = new JSONObject(t);

                json.put(buildResult.getJobId(), js);
            }

            System.out.println(json);

        } catch (JSONException e) {
            Activity.wrap(e);
        }

    }


}
