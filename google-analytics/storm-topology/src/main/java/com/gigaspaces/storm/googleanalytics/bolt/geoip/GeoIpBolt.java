package com.gigaspaces.storm.googleanalytics.bolt.geoip;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.googleanalytics.tools.GeoIpLookupService;

import java.util.Arrays;

/**
 * @author Oleksiy_Dyagilev
 */
public class GeoIpBolt extends BaseBasicBolt {

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String ip = tuple.getString(3);
        String country = GeoIpLookupService.getInstance().getCountry(ip);
        collector.emit(Arrays.<Object>asList(country));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("country"));
    }
}
