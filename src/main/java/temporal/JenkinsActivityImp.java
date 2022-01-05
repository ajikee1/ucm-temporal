package temporal;

import helper.ApiHelper;
import io.temporal.activity.Activity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class JenkinsActivityImp implements JenkinsActivity {

    private ApiHelper apiHelper;
    private String jenkinsIp;
    private String jenkinsPort;
    private String authHeader;

    @Override
    public String triggerJenkinsBuild(String jobId) {
        String buildStatus = null;
        apiHelper = new ApiHelper();

        Properties jenkinsProps = apiHelper.loadProperties("jenkins.properties");
        jenkinsIp = jenkinsProps.getProperty("jenkins_ip");
        jenkinsPort = jenkinsProps.getProperty("jenkins_port");
        authHeader = apiHelper.generateEncodedAuthHeader();

        String crumb = getCrumb();
        System.out.println("Crumb: " + crumb);

        if (crumb != null) {
            String locationUrl = buildJob(jobId, crumb);

            if (locationUrl != null) {
                String executableUrl = getExecutableUrl(locationUrl, jobId);

                if (executableUrl != null) {
                    buildStatus = getBuildStatus(executableUrl, jobId);
                }

            }
        }
        return buildStatus;
    }



    /* Get the Jenkins Crumb */
    public String getCrumb() {
        String crumb = null;
        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/crumbIssuer/api/json";

        HashMap<String, String> headersHm = new HashMap<>();
        headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);

        String httpResponse = apiHelper.runHttpGetRequest(url, headersHm);
        if (httpResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(httpResponse.toString());
                crumb = jsonObject.get("crumb").toString();
                return crumb;

            } catch (JSONException e) {
                Activity.wrap(e);
            }

        }
        return crumb;
    }

    /* Trigger the build of the Jenkins job using the crumb */
    public String buildJob(String jobId, String crumb) {
        String locationUrl = null;
        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/job/" + jobId + "/build";

        HashMap<String, String> headersHm = new HashMap<String, String>();
        headersHm.put("Jenkins-Crumb", crumb);
        headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);

        Map<HttpResponse, String> responseHashMap  = apiHelper.runHttpPostRequest(url, headersHm);

        HttpResponse httpResponse = null;
        for (Map.Entry e : responseHashMap.entrySet()) {
            httpResponse = (HttpResponse) e.getKey();
        }

        if (httpResponse != null) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            locationUrl = httpResponse.getFirstHeader("Location").getValue();

            System.out.println("Build Status: " + statusCode + " Build Location URL: " + locationUrl);

            if (statusCode == 201) {
                return locationUrl;
            }

        }
        return locationUrl;
    }

    /* Get the Executable URL from the Location URL */
    public String getExecutableUrl(String locationUrl, String jobId) {
        String executableUrl = null;
        Boolean executionStarted = false;
        long timeOutCounter = 0l;

        // Wait for job execution to start
        while (!executionStarted) {

            try {

                Thread.sleep(1000);
                timeOutCounter = (timeOutCounter + 1L);
                System.out.println("Waiting on the job " + jobId + " execution to start " + timeOutCounter + " seconds");

                Activity.getExecutionContext().heartbeat("Waiting on the job " + jobId + " execution to start " + timeOutCounter + " seconds");

                HashMap<String, String> headersHm = new HashMap<>();
                headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);

                String httpResponse = apiHelper.runHttpGetRequest(locationUrl + "api/json", headersHm);

                if (httpResponse != null) {
                    JSONObject jsonObject = new JSONObject(httpResponse.toString());

                    if (jsonObject.has("executable")) {
                        executionStarted = true;
                        JSONObject executableObject = jsonObject.getJSONObject("executable");
                        executableUrl = executableObject.get("url").toString();
                        return executableUrl;
                    }
                }

            } catch (Exception e) {
                Activity.wrap(e);
            }

        }

        return executableUrl;
    }

    /* Get the Job build status */
    public String getBuildStatus(String executionUrl, String jobId) {
        String buildResult = null;
        Boolean executionFinished = false;
        long timeOutCounter = 0l;

        // Wait for job execution to finish
        while (!executionFinished) {

            try {

                Thread.sleep(1000);
                timeOutCounter = (timeOutCounter + 1L);
                System.out.println("Waiting on the job " + jobId + " execution to finish " + timeOutCounter + " seconds");
                Activity.getExecutionContext().heartbeat("Waiting on the job " + jobId + " execution to finish " + timeOutCounter + " seconds");

                HashMap<String, String> headersHm = new HashMap<>();
                headersHm.put(HttpHeaders.AUTHORIZATION, authHeader);

                String httpResponse = apiHelper.runHttpGetRequest(executionUrl + "api/json", headersHm);

                if (httpResponse != null) {
                    JSONObject jsonObject = new JSONObject(httpResponse.toString());
                    String building = jsonObject.get("building").toString();

                    if (building == "false") {
                        executionFinished = true;
                        buildResult = jsonObject.get("result").toString();
                        return buildResult;
                    }
                }

            } catch (Exception e) {
                Activity.wrap(e);
            }

        }

        return buildResult;
    }
}
