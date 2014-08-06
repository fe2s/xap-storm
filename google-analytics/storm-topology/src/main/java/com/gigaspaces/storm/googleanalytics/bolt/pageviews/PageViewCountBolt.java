package com.gigaspaces.storm.googleanalytics.bolt.pageviews;

import backtype.storm.Config;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.googleanalytics.util.TupleHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Counts page views.
 *
 * @author Oleksiy_Dyagilev
 */
public class PageViewCountBolt extends BaseBasicBolt {

    private static Logger log = LoggerFactory.getLogger(PageViewCountBolt.class);

    private long count = 0;
    private int emitFrequencyInSeconds;

    public PageViewCountBolt(int emitFrequencyInSeconds) {
        this.emitFrequencyInSeconds = emitFrequencyInSeconds;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleHelpers.isTickTuple(tuple)) {
            log.debug("Received tick tuple, triggering emit");
            emitCount(collector);
        } else {
            trackTuple(tuple);
        }
    }

    private void emitCount(BasicOutputCollector collector) {
        collector.emit(Arrays.<Object>asList(count));
        count = 0;
    }

    private void trackTuple(Tuple tuple) {
        count++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("pageViewsCount"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
        return conf;
    }
}
