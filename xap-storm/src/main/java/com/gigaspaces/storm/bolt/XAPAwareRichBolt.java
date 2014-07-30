package com.gigaspaces.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import org.openspaces.core.GigaSpace;

import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public abstract class XAPAwareRichBolt extends BaseRichBolt {

    protected String xapSpaceUrl;
    protected GigaSpace space;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        // if not set explicitly, lookup in config
        if (xapSpaceUrl == null) {
            xapSpaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        }
        if (xapSpaceUrl == null){
            throw new RuntimeException("XAP space url is not set");
        }

        this.space = GigaSpaceFactory.getInstance(xapSpaceUrl);
    }

    public void setXapSpaceUrl(String xapSpaceUrl) {
        this.xapSpaceUrl = xapSpaceUrl;
    }
}
