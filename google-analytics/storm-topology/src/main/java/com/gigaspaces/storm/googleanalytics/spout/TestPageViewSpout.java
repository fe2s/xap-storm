package com.gigaspaces.storm.googleanalytics.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import java.util.*;

/**
 * Page views mock spout. Used for test topology without XAP.
 *
 * @author Oleksiy_Dyagilev
 */
public class TestPageViewSpout extends BaseRichSpout {

    private static final Map<String, Integer> URLS_PROBABILITY = new HashMap<String, Integer>() {{
        put("http://www.gigaspaces.com/services-offering-overview", 10);
        put("http://www.gigaspaces.com/about", 8);
        put("http://www.gigaspaces.com/support-center", 5);
        put("http://www.gigaspaces.com/xap-download", 5);
        put("http://www.gigaspaces.com/cloudify-cloud-orchestration/overview", 1);
    }};

    private static final Map<String, Integer> REFERRALS_PROBABILITY = new HashMap<String, Integer>() {{
        put("https://www.google.com/#q=gigaspace", 10);
        put("https://www.google.com/#q=xap", 8);
        put("https://www.yahoo.com/query=gigaspace", 3);
        put("https://www.yahoo.com/query=datagrid", 1);
    }};

    private static final Map<String, Integer> SESSIONS_PROBABILITY = new HashMap<String, Integer>() {{
        put("SESSION1", 10);
        put("SESSION2", 8);
        put("SESSION3", 3);
        put("SESSION4", 1);
    }};

    private static final List<String> urls = new ArrayList<String>();
    private static final List<String> referrals = new ArrayList<String>();
    private static final List<String> sessions = new ArrayList<String>();


    private SpoutOutputCollector collector;
    private Random random = new Random();

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("url", "referral", "session", "ip"));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        populateList(urls, URLS_PROBABILITY);
        populateList(referrals, REFERRALS_PROBABILITY);
        populateList(sessions, SESSIONS_PROBABILITY);
    }

    @Override
    public void nextTuple() {
        Utils.sleep(100);
        String url = random(urls);
        String referral = random(referrals);
        String session = random(sessions);
        String ip = "151.38.39.114";
        collector.emit(Arrays.<Object>asList(url, referral, session, ip));
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
