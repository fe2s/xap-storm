package com.gigaspaces.storm.googleanalytics.bolt.activeusers;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.googleanalytics.tools.LastSeenTracker;
import com.gigaspaces.storm.googleanalytics.util.TupleHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Keeps track of user last seen time and emits number of active users for this partition.
 *
 * @author Oleksiy_Dyagilev
 */
public class PartitionedActiveUsersBolt extends BaseBasicBolt {

    private static Logger log = LoggerFactory.getLogger(PartitionedActiveUsersBolt.class);

    private LastSeenTracker<String> lastSeenUsersTracker;
    private int emitFrequencyInSeconds;
    private int activePeriodInSeconds;

    public PartitionedActiveUsersBolt(int emitFrequencyInSeconds, int activePeriodInSeconds) {
        this.emitFrequencyInSeconds = emitFrequencyInSeconds;
        this.activePeriodInSeconds = activePeriodInSeconds;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        lastSeenUsersTracker = new LastSeenTracker<>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleHelpers.isTickTuple(tuple)) {
            log.debug("Received tick tuple, triggering emit of partitioned number active users");
            emitActiveUsersNumber(collector);
        } else {
            trackTuple(tuple);
        }
    }

    private void emitActiveUsersNumber(BasicOutputCollector collector) {
        int activeUsersNumber = lastSeenUsersTracker.numberOfSeenInLastNSeconds(activePeriodInSeconds);
        collector.emit(Arrays.<Object>asList(activeUsersNumber));
    }

    private void trackTuple(Tuple tuple) {
        String session = tuple.getString(2);
        lastSeenUsersTracker.track(session);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("activeUsersSet"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
        return conf;
    }
}
