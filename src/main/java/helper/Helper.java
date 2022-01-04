package helper;

import org.apache.commons.codec.binary.Base64;
import temporal.UcmWorkflowInit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Helper {

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
}
