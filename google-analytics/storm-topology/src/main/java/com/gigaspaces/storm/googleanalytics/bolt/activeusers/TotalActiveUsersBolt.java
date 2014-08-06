package com.gigaspaces.storm.googleanalytics.bolt.activeusers;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.storm.bolt.XAPAwareBasicBolt;
import com.gigaspaces.storm.googleanalytics.model.reports.ActiveUsersReport;
import com.gigaspaces.storm.googleanalytics.model.reports.OverallReport;
import com.gigaspaces.storm.googleanalytics.util.TupleHelpers;
import com.j_spaces.core.client.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a map of [source_task, users_count] and emits the total count for all sources.
 * Writes report to XAP.
 *
 * @author Oleksiy_Dyagilev
 */
public class TotalActiveUsersBolt extends XAPAwareBasicBolt {

    private static Logger log = LoggerFactory.getLogger(TotalActiveUsersBolt.class);

    private int emitFrequencyInSeconds;
    private Map<Integer, Integer> activeUsersPerPartition;

    public TotalActiveUsersBolt(int emitFrequencyInSeconds) {
        this.emitFrequencyInSeconds = emitFrequencyInSeconds;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.activeUsersPerPartition = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleHelpers.isTickTuple(tuple)) {
            log.debug("Received tick tuple, triggering emit of total number active users");
            emitActiveUsersNumber(collector);
        } else {
            trackTuple(tuple);
        }
    }

    private void trackTuple(Tuple tuple) {
        int sourceTask = tuple.getSourceTask();
        activeUsersPerPartition.put(sourceTask, tuple.getInteger(0));
    }

    private void emitActiveUsersNumber(BasicOutputCollector collector) {
        long totalActiveUsers = 0;
        for (Integer n : activeUsersPerPartition.values()) {
            totalActiveUsers += n;
        }
        log.debug("Total number of active users " + totalActiveUsers);
        collector.emit(Arrays.<Object>asList(totalActiveUsers));

        space.change(new SQLQuery<>(OverallReport.class, "id = 'gigaspaces.com'"), new ChangeSet().set("activeUsersReport", new ActiveUsersReport(totalActiveUsers)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("totalActiveUsers"));
    }

    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
        return conf;
    }
}
