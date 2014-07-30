package com.gigaspaces.storm.trident.state;

import backtype.storm.task.IMetricsContext;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.config.ConfigConstants;
import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.state.StateType;
import storm.trident.state.map.*;

import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPStateFactory implements StateFactory {
    private StateType type;
    private String xapSpaceUrl;

    private XAPStateFactory(StateType type) {
        this.type = type;
    }

    public static StateFactory opaque() {
        return new XAPStateFactory(StateType.OPAQUE);
    }

    public static StateFactory transactional() {
        return new XAPStateFactory(StateType.TRANSACTIONAL);
    }

    public static StateFactory nonTransactional() {
        return new XAPStateFactory(StateType.NON_TRANSACTIONAL);
    }

    @Override
    public State makeState(Map conf, IMetricsContext metricsContext, int partitionIndex, int numPartitions) {
        // if not set explicitly, lookup in config
        if (xapSpaceUrl == null) {
            xapSpaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        }

        XAPState xapState = new XAPState(xapSpaceUrl);

        MapState ms;
        if (type == StateType.NON_TRANSACTIONAL) {
            ms = NonTransactionalMap.build(xapState);
        } else if (type == StateType.OPAQUE) {
            ms = OpaqueMap.build(xapState);
        } else if (type == StateType.TRANSACTIONAL) {
            ms = TransactionalMap.build(xapState);
        } else {
            throw new RuntimeException("Unknown state type: " + type);
        }
        return new SnapshottableMap(ms, new Values("$GLOBAL$"));
    }

    public XAPStateFactory setXapSpaceUrl(String xapSpaceUrl) {
        this.xapSpaceUrl = xapSpaceUrl;
        return this;
    }
}
