package com.gigaspaces.storm.googleanalytics.bolt.urls;

import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.googleanalytics.bolt.generic.RollingCountBolt;

/**
 * Rolling count for urls.
 *
 * @author Oleksiy_Dyagilev
 */
public class UrlRollingCountBolt extends RollingCountBolt<String> {
    public UrlRollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
        super(windowLengthInSeconds, emitFrequencyInSeconds);
    }

    @Override
    protected String extractCountableObject(Tuple tuple) {
        return tuple.getString(0);
    }
}
