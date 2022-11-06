package org.ljmu.thesis.helpers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class ApiHelper {
    private static final Logger LOGGER = Logger.getLogger(ApiHelper.class.getName());

    public static String get(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            String stringOutput = EntityUtils.toString(response.getEntity());
            if (stringOutput.startsWith(")")) {
                return stringOutput.substring(4);
            }
            return stringOutput;
        } catch (Exception e) {
            LOGGER.info(String.format("Exception occurred while Gerrit's GET call for URI: %s with message: %s\nNow retrying...!!!", uri, e.getMessage()));
            return get(uri);
        }
    }
}
