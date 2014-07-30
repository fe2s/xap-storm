package com.gigaspaces.storm.googleanalytics.feeder;


import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Mykola_Zalyayev
 */
@Component
public class PageViewFeeder {

    private Random r = new Random();

    private static final Map<String, Integer> URLS_PROBABILITY = new HashMap<String, Integer>() {{
        put("http://www.gigaspaces.com/services-offering-overview", 10);
        put("http://www.gigaspaces.com/about", 8);
        put("http://www.gigaspaces.com/support-center", 5);
        put("http://www.gigaspaces.com/xap-download", 5);
        put("http://www.gigaspaces.com/cloudify-cloud-orchestration/overview", 1);
        put("http://www.gigaspaces.com/", 10);
        put("http://www.gigaspaces.com/cloudify-cloud-automation/go-pro", 1);
        put("http://www.gigaspaces.com/Recentreleases", 2);
        put("http://www.gigaspaces.com/xap-memoryxtend-flash-performance-big-data", 4);
        put("http://www.gigaspaces.com/upcomingevents", 2);
    }};

    private static final Map<String, Integer> REFERRALS_PROBABILITY = new HashMap<String, Integer>() {{
        put("https://www.google.com/#q=gigaspace", 10);
        put("https://www.google.com/#q=xap", 8);
        put("https://www.yahoo.com/query=gigaspace", 3);
        put("https://www.yahoo.com/query=datagrid", 1);
        put("http://en.wikipedia.org/wiki/GigaSpaces", 2);
        put("http://docs.gigaspaces.com/", 5);
        put("http://wiki.gigaspaces.com/wiki/display/XAP96", 3);
        put("https://mail.google.com/mail/u/1/#inbox", 4);
        put("http://www.gigaspaces.com/", 8);
        put("http://www.gigaspaces.com/user/register?destination=node%2F1", 6);
    }};

    private static final Map<String, Integer> IPS_PROBABILITY = new HashMap<String, Integer>() {{
       put("3.255.255.255",10);
       put("117.91.92.052",8);
       put("151.38.39.114",3);
       put("198.162.54.153",5);
       put("121.44.22.177",1);
    }};

    private List<String> urls = new ArrayList<>();
    private List<String> referrals = new ArrayList<>();
    private List<String> sessions = new ArrayList<>();
    private Map<String, String> ips = new HashMap<>();

    private Random random = new Random(System.currentTimeMillis());

    public PageViewFeeder() {
        populateList(urls, URLS_PROBABILITY);
        populateList(referrals, REFERRALS_PROBABILITY);
        populateSessions();
        populateIps();
    }

    public PageView nextRequest() {
        PageView pageView = new PageView();
        pageView.setPage(random(urls));
        pageView.setReferral(random(referrals));
        pageView.setSessionId(random(sessions));
        pageView.setIp(ips.get(pageView.getSessionId()));
        return pageView;
    }

    public List<PageView> nextRequestsList(int count){
        List<PageView> pageViewList = new ArrayList<>(count);
        for(int i = 0;i<count;i++){
            pageViewList.add(nextRequest());
        }
        return pageViewList;
    }

    private void populateSessions(){
        for(int i = 0; i<1000; i++){
            for(int j = 0; j< (Math.random()*10-1)+1; j++){
                sessions.add("sessionid"+i);
            }
        }
    }

    private void populateIps(){
        for(String sessionId: sessions){
            ips.put(sessionId,IpConverter.longToIp(r.nextLong()));
        }
    }

    private void populateList(List<String> list, Map<String, Integer> probabilities) {
        for (String item : probabilities.keySet()) {
            Integer prob = probabilities.get(item);
            list.addAll(Collections.nCopies(prob, item));
        }
    }

    private String random(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
