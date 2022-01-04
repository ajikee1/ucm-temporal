package temporal;

import helper.Helper;
import io.temporal.activity.Activity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class JenkinsActivityImp implements JenkinsActivity {

    private String jenkinsIp;
    private String jenkinsPort;
    private String authHeader;

    @Override
    public String triggerJenkinsBuild(String jobId) {
        String locationUrl = null;

        Helper help = new Helper();

        Properties jenkinsProps = help.loadProperties("jenkins.properties");
        jenkinsIp = jenkinsProps.getProperty("jenkins_ip");
        jenkinsPort = jenkinsProps.getProperty("jenkins_port");

        String line = null;
        StringBuffer result = new StringBuffer();

        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/crumbIssuer/api/json";
        HttpGet get = new HttpGet(url);

        authHeader = help.generateEncodedAuthHeader();
        get.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = reader.readLine()) != null) {
                result.append(line.toString());
            }

            JSONObject jsonObject = new JSONObject(result.toString());
            String crumb = jsonObject.get("crumb").toString();
            System.out.println("Crumb: " + crumb);

            if (crumb != null) {
                locationUrl = triggerBuild(jobId, crumb);
                return locationUrl;
            }

        } catch (IOException | JSONException ie) {
            Activity.wrap(ie);
        }

        return locationUrl;
    }

    @Override
    public String executableUrlFromLocationUrl(String locationUrl) {

        locationUrl = locationUrl + "api/json";

        String executableUrl = null;

        Boolean executionStarted = false;
        long timeOutCounter = 0l;

        /* Wait for the execution URL to show up */
        while (!executionStarted) {

            String line = null;
            StringBuffer result = new StringBuffer();

            HttpGet get = new HttpGet(String.valueOf(locationUrl));
            get.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

            HttpClient client = HttpClientBuilder.create().build();

            try {
                Thread.sleep(1000);
                timeOutCounter = (timeOutCounter + 1L);
                System.out.println("Waiting on the job execution to start " + timeOutCounter + " seconds");
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                while ((line = reader.readLine()) != null) {
                    result.append(line.toString());
                }

                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.has("executable")) {
                    executionStarted = true;
                    JSONObject executableObject = jsonObject.getJSONObject("executable");
                    executableUrl = executableObject.get("url").toString();
                    System.out.println("Executable URL: " + executableUrl);

                    return executableUrl;
                }

            } catch (IOException | InterruptedException | JSONException ie) {
                Activity.wrap(ie);
            }
        }
        return executableUrl;
    }


    public String triggerBuild(String jobId, String crumb) {
        String location = null;
        String line;
        StringBuffer result = new StringBuffer();

        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/job/" + jobId + "/build";

        HttpPost post = new HttpPost(url);
        post.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        post.setHeader("Jenkins-Crumb", crumb);
        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpResponse response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            location = response.getFirstHeader("Location").getValue();

            System.out.println("Build Status: " + statusCode + " Build Location URL: " + location);

            if (statusCode == 201) {
                return location;
            }


        } catch (IOException ie) {
            Activity.wrap(ie);
        }

        return location;
    }
}
