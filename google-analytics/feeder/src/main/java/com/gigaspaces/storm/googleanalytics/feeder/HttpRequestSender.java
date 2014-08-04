package com.gigaspaces.storm.googleanalytics.feeder;


import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

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
     * Send one {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView} request to specific host.
     *
     * @param feederRequest - {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView}
     * @param host - host to rest-service
     *
     * @throws Exception - if not able to send request to server.
     */
    public void sendRequest(PageView feederRequest, String host) throws Exception {
        send(feederRequest,host+"/rest-service/rest/trackPageView");
    }

    /**
     * Send list of {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView} requests to specific host.
     *
     * @param feederRequest - list of {@link com.gigaspaces.storm.googleanalytics.model.feeder.PageView}
     * @param host - host to rest-service
     *
     * @throws Exception - if not able to send request to server.
     */
    public void sendRequest(List<PageView> feederRequest, String host) throws Exception {
        send(feederRequest,host+"/rest-service/rest/trackPageViewList");
    }

    private void send(Object feederRequest, String url) throws Exception {

        String jsonValue = mapper.writeValueAsString(feederRequest);

        HttpClient client = new DefaultHttpClient();

        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(jsonValue);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        HttpResponse response = client.execute(request);
    }
}
