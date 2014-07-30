package com.gigaspaces.storm.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IBasicBolt;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import org.openspaces.core.GigaSpace;

import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public abstract class XAPAwareBasicBolt implements IBasicBolt {

    protected String xapSpaceUrl;
    protected GigaSpace space;

    @Override
    public void prepare(Map conf, TopologyContext context) {
        // if not set explicitly, lookup in config
        if (xapSpaceUrl == null) {
            xapSpaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        }
        if (xapSpaceUrl == null){
            throw new RuntimeException("XAP space url is not set");
        }

        this.space = GigaSpaceFactory.getInstance(xapSpaceUrl);
    }

    @Override
    public void cleanup() {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    public void setXapSpaceUrl(String xapSpaceUrl) {
        this.xapSpaceUrl = xapSpaceUrl;
    }
}
