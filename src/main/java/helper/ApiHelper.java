package helper;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import temporal.UcmWorkflowInit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApiHelper {

    public Map<HttpResponse, String> runHttpPostRequest(String apiRequest, Map<String, String> headersMap) {
        String line;
        HttpResponse httpResponse = null;
        StringBuffer result = new StringBuffer();

        Map<HttpResponse, String> responseHashMap = new HashMap<>();

        HttpPost post = new HttpPost(apiRequest);

        if (!headersMap.isEmpty()) {
            for (Map.Entry header : headersMap.entrySet()) {
                post.setHeader(header.getKey().toString(), header.getValue().toString());
            }
        }

        HttpClient client = HttpClientBuilder.create().build();

        try {
            httpResponse = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            while ((line = reader.readLine()) != null) {
                result.append(line.toString());
            }

            responseHashMap.put(httpResponse, result.toString());

        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return responseHashMap;
    }

    public String runHttpGetRequest(String apiRequest, Map<String, String> headersMap) {
        String line;
        StringBuffer result = new StringBuffer();

        HttpGet get = new HttpGet(apiRequest);

        if (!headersMap.isEmpty()) {
            for (Map.Entry header : headersMap.entrySet()) {
                get.setHeader(header.getKey().toString(), header.getValue().toString());
            }
        }

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while ((line = reader.readLine()) != null) {
                result.append(line.toString());
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return result.toString();
    }

    public String generateEncodedAuthHeader() {
        Properties jenkinsProps = loadProperties("jenkins.properties");
        String jenkinsUser = jenkinsProps.getProperty("jenkins_user");
        String jenkinsPassword = jenkinsProps.getProperty("jenkins_token");

        /* Basic Authentication */
        String auth = jenkinsUser + ":" + jenkinsPassword;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        return authHeader;
    }

    public String generateEncodedAuthHeaderJira() {
        Properties jiraProps = loadProperties("jira.properties");
        String jiraUser = jiraProps.getProperty("jira_user");
        String jiraToken = jiraProps.getProperty("jira_token");

        /* Basic Authentication */
        String auth = jiraUser + ":" + jiraToken;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        return authHeader;
    }

    public Properties loadProperties(String propertiesFileName_) {
        Properties properties = new Properties();
        InputStream in = UcmWorkflowInit.class.getClassLoader().getResourceAsStream(propertiesFileName_);

        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
