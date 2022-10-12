package org.ljmu.thesis.crsmells.helpers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestHelper {
    public static String get(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            String stringOutput = EntityUtils.toString(response.getEntity());
            if (stringOutput.startsWith(")")) {
                return stringOutput.substring(4);
            }
            return stringOutput;
        }
    }
}
