package org.ljmu.thesis.crsmells;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestHelper {

    public static void main(String[] args) throws IOException {
        HttpGet request = new HttpGet("https://git.eclipse.org/r/changes/I6f6cd55a482cff769a5059c8e8362ce2102b5f3c/revisions/2/commit");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String responseOutput = EntityUtils.toString(entity);
            System.out.println(responseOutput);
        }
    }
}
