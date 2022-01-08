package temporal.Jira;

import com.google.gson.Gson;
import helper.ApiHelper;
import io.temporal.activity.Activity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
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
    public void addResultsToJira(String issueId, List<buildResultsDAO> buildResults) {

        ApiHelper apiHelper = new ApiHelper();

        Properties jiraProps = apiHelper.loadProperties("jira.properties");
        String jiraDomain = jiraProps.getProperty("jira_domain");
        String authHeader = apiHelper.generateEncodedAuthHeaderJira();

        String ticketStatus = null;
        String url = jiraDomain + "/rest/api/2/issue/" + issueId + "/comment";

        HashMap<String, String> headersHm = new HashMap<>();
        headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);
        headersHm.put("Content-type", "application/json");

        try {
            JSONObject json = new JSONObject();

            for (buildResultsDAO buildResult : buildResults) {
                String t = new Gson().toJson(buildResult);
                JSONObject js = new JSONObject(t);

                json.put(buildResult.getJobId(), js);
            }

            JSONObject postJson = new JSONObject();
            postJson.put("body", "{code}" + json.toString() + "{code}");
            Map<HttpResponse, String> responseHashMap = apiHelper.runHttpPostRequestWithRequestBody(url, headersHm, postJson);

            HttpResponse httpResponse = null;
            for (Map.Entry e : responseHashMap.entrySet()) {
                httpResponse = (HttpResponse) e.getKey();
            }

            if (httpResponse != null) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if(statusCode == 201){
                    System.out.println("Job build results added to ticket " + jiraDomain + "/rest/api/2/issue/" + issueId);
                }
            }

        } catch (Exception e) {
            Activity.wrap(e);
        }

    }


}
