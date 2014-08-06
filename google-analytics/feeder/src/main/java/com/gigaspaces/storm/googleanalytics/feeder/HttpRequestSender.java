package com.gigaspaces.storm.googleanalytics.feeder;


import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Class taht use Apache http client for sending post request to rest-service.
 *
 * @author Mykola_Zalyayev
 */
@Component
public class HttpRequestSender {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Send list of {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView} requests to specific host.
     *
     * @param feederRequest - list of {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView}
     * @param host          - host to rest-service
     * @throws Exception - if not able to send request to server.
     */
    public void sendRequest(List<PageView> feederRequest, String host) throws Exception {

        String jsonValue = mapper.writeValueAsString(feederRequest);
        String url = host + "/rest-service/rest/trackPageViewList";

        HttpClient client = new DefaultHttpClient();

        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(jsonValue);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        HttpResponse response = client.execute(request);
    }

    public void sendCreateSiteRequest(String siteId, String host) throws IOException {

        HttpClient client = new DefaultHttpClient();

        HttpPut request = new HttpPut(host + "/rest-service/rest/register/");
        StringEntity params = new StringEntity(siteId);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        HttpResponse response = client.execute(request);
    }
}
