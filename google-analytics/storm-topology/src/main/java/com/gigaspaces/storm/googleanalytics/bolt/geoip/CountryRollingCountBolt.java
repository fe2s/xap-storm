package com.gigaspaces.storm.googleanalytics.bolt.geoip;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import com.gigaspaces.storm.googleanalytics.bolt.generic.RollingCountBolt;
import com.gigaspaces.storm.googleanalytics.model.reports.GeoReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class CountryRollingCountBolt extends RollingCountBolt<String> {

    private static Logger log = LoggerFactory.getLogger(CountryRollingCountBolt.class);

    public CountryRollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
        super(windowLengthInSeconds, emitFrequencyInSeconds);
    }

    @Override
    protected void emit(Map<String, Long> counts, int actualWindowLengthInSeconds) {
        collector.emit(Arrays.<Object>asList(counts));

        // write to space
        GeoReport geoReport = new GeoReport();
        geoReport.setCountryCountMap(counts);
        space.write(geoReport);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("countryCountMap"));
    }

}