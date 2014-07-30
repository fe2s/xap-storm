package com.gigaspaces.storm.googleanalytics.bolt.pageviews;

import backtype.storm.Config;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.bolt.XAPAwareBasicBolt;
import com.gigaspaces.storm.googleanalytics.model.reports.PageViewTimeSeriesReport;
import com.gigaspaces.storm.googleanalytics.tools.SlidingWindowCounter;
import com.gigaspaces.storm.googleanalytics.util.TupleHelpers;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class PageViewTimeSeriesBolt extends XAPAwareBasicBolt {

    private static final Logger LOG = Logger.getLogger(PageViewTimeSeriesBolt.class);

    private int windowLengthInSeconds;
    private int emitFrequencyInSeconds;
    private SlidingWindowCounter<Object> counter;
    private static final Object PAGE_VIEW = new Object();

    public PageViewTimeSeriesBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
        this.windowLengthInSeconds = windowLengthInSeconds;
        this.emitFrequencyInSeconds = emitFrequencyInSeconds;
        this.counter = new SlidingWindowCounter<Object>(deriveNumWindowChunksFrom(this.windowLengthInSeconds,
                this.emitFrequencyInSeconds));
    }

    private int deriveNumWindowChunksFrom(int windowLengthInSeconds, int windowUpdateFrequencyInSeconds) {
        return windowLengthInSeconds / windowUpdateFrequencyInSeconds;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleHelpers.isTickTuple(tuple)) {
            LOG.debug("Received tick tuple, triggering emit");
            emitCurrentWindow(collector);
        }
        else {
            countObj(tuple);
        }
    }

    private void countObj(Tuple tuple) {
        long count = tuple.getLong(0);
        counter.incrementCount(PAGE_VIEW, count);
    }

    private void emitCurrentWindow(BasicOutputCollector collector) {
        long[] window = counter.getOrderedWindowThenAdvance(PAGE_VIEW);

        PageViewTimeSeriesReport report = new PageViewTimeSeriesReport();
        report.setSlotLengthInSeconds(emitFrequencyInSeconds);
        report.setWindowLengthInSeconds(windowLengthInSeconds);
        report.setCounts(window);

        collector.emit(Arrays.<Object>asList(report));

        // write to xap
        space.write(report);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("pageViewsWindow"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
        return conf;
    }

}
