package com.gigaspaces.storm.trident.state.readonly;

import backtype.storm.task.IMetricsContext;
import com.gigaspaces.storm.config.ConfigConstants;
import com.j_spaces.core.client.SQLQuery;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPReadOnlyStateFactory<T> implements StateFactory {

    private LookupStrategy lookupStrategy;
    private String xapSpaceUrl;
    private Class<T> clazz;
    private SQLQuery<T> sqlQuery;
    private String[] sqlQueryProjections; // we pass them separately because projections are transient in SQLQuery

    private XAPReadOnlyStateFactory() {
    }

    public static <T> XAPReadOnlyStateFactory byIds(Class<T> clazz) {
        XAPReadOnlyStateFactory<T> factory = new XAPReadOnlyStateFactory<T>();
        factory.lookupStrategy = LookupStrategy.BY_IDS;
        factory.clazz = clazz;
        return factory;
    }

    public static <T> XAPReadOnlyStateFactory bySqlQuery(SQLQuery<T> sqlQuery) {
        XAPReadOnlyStateFactory<T> factory = new XAPReadOnlyStateFactory<T>();
        factory.lookupStrategy = LookupStrategy.SQL_QUERY;
        factory.sqlQuery = sqlQuery;
        factory.sqlQueryProjections = sqlQuery.getProjections();
        return factory;
    }

    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        // if not set explicitly, lookup in config
        if (xapSpaceUrl == null) {
            xapSpaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        }

        if (lookupStrategy == LookupStrategy.BY_IDS) {
            return XAPReadOnlyState.byIds(xapSpaceUrl, clazz);
        }
        if (lookupStrategy == LookupStrategy.SQL_QUERY) {
            return XAPReadOnlyState.sqlQuery(xapSpaceUrl, sqlQuery, sqlQueryProjections);
        }

        throw new RuntimeException("Unknown lookup strategy " + lookupStrategy);
    }

    public XAPReadOnlyStateFactory<T> setXapSpaceUrl(String xapSpaceUrl) {
        this.xapSpaceUrl = xapSpaceUrl;
        return this;
    }
}
