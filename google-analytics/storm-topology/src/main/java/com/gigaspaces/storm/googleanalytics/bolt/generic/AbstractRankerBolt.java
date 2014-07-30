package com.gigaspaces.storm.googleanalytics.bolt.generic;

import backtype.storm.Config;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.bolt.XAPAwareBasicBolt;
import com.gigaspaces.storm.googleanalytics.tools.*;
import com.gigaspaces.storm.googleanalytics.util.TupleHelpers;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract bolt provides the basic behavior of bolts that rank objects according to their count.
 * <p/>
 * It uses a template method design pattern for {@link AbstractRankerBolt#execute(Tuple, BasicOutputCollector)} to allow
 * actual bolt implementations to specify how incoming tuples are processed, i.e. how the objects embedded within those
 * tuples are retrieved and counted.
 */
public abstract class AbstractRankerBolt extends XAPAwareBasicBolt {

  private static final long serialVersionUID = 4931640198501530202L;

  private final int emitFrequencyInSeconds;
  private final int count;
  private final Rankings rankings;


  public AbstractRankerBolt(int topN, int emitFrequencyInSeconds) {
    if (topN < 1) {
      throw new IllegalArgumentException("topN must be >= 1 (you requested " + topN + ")");
    }
    if (emitFrequencyInSeconds < 1) {
      throw new IllegalArgumentException(
          "The emit frequency must be >= 1 seconds (you requested " + emitFrequencyInSeconds + " seconds)");
    }
    count = topN;
    this.emitFrequencyInSeconds = emitFrequencyInSeconds;
    rankings = new Rankings(count);
  }

  protected Rankings getRankings() {
    return rankings;
  }

  /**
   * This method functions as a template method (design pattern).
   */
  @Override
  public final void execute(Tuple tuple, BasicOutputCollector collector) {
    if (TupleHelpers.isTickTuple(tuple)) {
      getLogger().debug("Received tick tuple, triggering emit of current rankings");
      emitRankings(collector);
    }
    else {
      updateRankingsWithTuple(tuple);
    }
  }

  protected abstract void updateRankingsWithTuple(Tuple tuple);

  protected void emitRankings(BasicOutputCollector collector) {
    collector.emit(new Values(rankings.copy()));
    getLogger().debug("Rankings: " + rankings);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("rankings"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    Map<String, Object> conf = new HashMap<String, Object>();
    conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
    return conf;
  }

  protected abstract Logger getLogger();
}
