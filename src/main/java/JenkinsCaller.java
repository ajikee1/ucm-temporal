import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class JenkinsCaller {

    private String jobId;
    private String crumb;
    private Properties jenkinsProps;
    private String jenkinsIp;
    private String jenkinsPort;

    public JenkinsCaller(String jobId) {
        this.jobId = jobId;
    }

    public String buildJob() {
        jenkinsProps = loadProperties("jenkins.properties");
        jenkinsIp = jenkinsProps.getProperty("jenkins_ip");
        jenkinsPort = jenkinsProps.getProperty("jenkins_port");

        getCrumb();
        triggerBuild();

        return "";
    }


    private String generateEncodedAuthHeader() {
        String jenkinsUser = jenkinsProps.getProperty("jenkins_user");
        String jenkinsPassword = jenkinsProps.getProperty("jenkins_token");

        /* Basic Authentication */
        String auth = jenkinsUser + ":" + jenkinsPassword;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        return authHeader;
    }


    void getCrumb() {
        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/crumbIssuer/api/json";

        String line = null;
        StringBuffer result = new StringBuffer();

        HttpGet get = new HttpGet(url);

        String authHeader = generateEncodedAuthHeader();
        get.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = reader.readLine()) != null) {
                result.append(line.toString());
            }

            JSONObject jsonObject = new JSONObject(result.toString());
            crumb = jsonObject.get("crumb").toString();

            System.out.println("Crumb: " + crumb);

        } catch (IOException | JSONException ie) {
            ie.printStackTrace();
        }

    }

    void triggerBuild() {
        String url = "http://" + jenkinsIp + ":" + jenkinsPort + "/job/" + jobId + "/build";

        String line;
        StringBuffer result = new StringBuffer();

        HttpPost post = new HttpPost(url);

        String authHeader = generateEncodedAuthHeader();
        post.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        post.setHeader("Jenkins-Crumb", crumb);

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpResponse response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            String location = response.getFirstHeader("Location").getValue();

            System.out.println("Build Status: " + statusCode);

            executableUrlFromLocationUrl(location);


        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public String executableUrlFromLocationUrl(String locationUrl) {
        locationUrl = locationUrl + "api/json";

        String executableUrl = null;

        Boolean executionStarted = false;
        long timeOutCounter = 0l;

        /* Wait for the execution URL to show up */
        while(!executionStarted){

            String line = null;
            StringBuffer result = new StringBuffer();

            HttpGet get = new HttpGet(String.valueOf(locationUrl));

            String authHeader = generateEncodedAuthHeader();
            get.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

            HttpClient client = HttpClientBuilder.create().build();

            try {
                Thread.sleep(1000);
                timeOutCounter = (timeOutCounter + 1L);
                System.out.println("Waiting on the job execution to start " + timeOutCounter);
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                while ((line = reader.readLine()) != null) {
                    result.append(line.toString());
                }

                JSONObject jsonObject = new JSONObject(result.toString());

                if(jsonObject.has("executable")){
                    executionStarted = true;
                    JSONObject executableObject = jsonObject.getJSONObject("executable");
                    executableUrl = executableObject.get("url").toString();
                    return executableUrl;
                }

            } catch (IOException | InterruptedException | JSONException ie) {
                ie.printStackTrace();
            }

        }

        return executableUrl;
    }


    Properties loadProperties(String propertiesFileName_) {
        Properties properties = new Properties();
        InputStream in = driver.class.getClassLoader().getResourceAsStream(propertiesFileName_);

        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
