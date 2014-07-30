package com.gigaspaces.storm.googleanalytics.bolt.referrals;

import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.googleanalytics.bolt.generic.RollingCountBolt;

/**
 * @author Oleksiy_Dyagilev
 */
public class ReferralRollingCountBolt extends RollingCountBolt<String> {

    public ReferralRollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
        super(windowLengthInSeconds, emitFrequencyInSeconds);
    }

    @Override
    protected String extractCountableObject(Tuple tuple) {
        return tuple.getString(1);
    }
}
