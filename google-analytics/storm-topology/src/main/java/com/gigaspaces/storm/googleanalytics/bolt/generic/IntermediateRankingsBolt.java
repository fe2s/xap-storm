package com.gigaspaces.storm.googleanalytics.bolt.generic;

import backtype.storm.tuple.Tuple;
import org.apache.log4j.Logger;
import com.gigaspaces.storm.googleanalytics.tools.*;

/**
 * This bolt ranks incoming objects by their count.
 * <p/>
 * It assumes the input tuples to adhere to the following format: (object, object_count, additionalField1,
 * additionalField2, ..., additionalFieldN).
 */
public final class IntermediateRankingsBolt extends AbstractRankerBolt {

  private static final long serialVersionUID = -1369800530256637409L;
  private static final Logger LOG = Logger.getLogger(IntermediateRankingsBolt.class);

  public IntermediateRankingsBolt(int topN, int emitFrequencyInSeconds) {
    super(topN, emitFrequencyInSeconds);
  }

  @Override
  protected void updateRankingsWithTuple(Tuple tuple) {
    Rankable rankable = RankableObjectWithFields.from(tuple);
    super.getRankings().updateWith(rankable);
  }

  @Override
  protected Logger getLogger() {
    return LOG;
  }
}
