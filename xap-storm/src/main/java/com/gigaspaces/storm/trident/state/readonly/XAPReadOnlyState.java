package com.gigaspaces.storm.trident.state.readonly;

import com.gigaspaces.client.ReadByIdsResult;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import storm.trident.state.ReadOnlyState;
import storm.trident.state.map.ReadOnlyMapState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPReadOnlyState<T> extends ReadOnlyState implements ReadOnlyMapState<T> {

    private GigaSpace space;
    private LookupStrategy lookupStrategy;
    private SQLQuery<T> sqlQuery;
    private Class<T> clazz;

    private XAPReadOnlyState() {
    }

    public static <T> XAPReadOnlyState<T> byIds(String spaceUrl, Class<T> clazz) {
        XAPReadOnlyState<T> state = new XAPReadOnlyState<T>();
        state.lookupStrategy = LookupStrategy.BY_IDS;
        state.space = GigaSpaceFactory.getInstance(spaceUrl);
        state.clazz = clazz;
        return state;
    }

    public static <T> XAPReadOnlyState<T> sqlQuery(String spaceUrl, SQLQuery<T> sqlQuery, String[] sqlQueryProjections) {
        XAPReadOnlyState<T> state = new XAPReadOnlyState<T>();
        state.lookupStrategy = LookupStrategy.SQL_QUERY;
        state.space = GigaSpaceFactory.getInstance(spaceUrl);
        state.sqlQuery = sqlQuery;
        // reattach projections because they are declared as transient in SQLQuery
        state.sqlQuery.setProjections(sqlQueryProjections);
        return state;
    }

    @Override
    public List<T> multiGet(List<List<Object>> keys) {
        if (lookupStrategy == LookupStrategy.BY_IDS) {
            return multiGeyByIds(keys);
        }
        if (lookupStrategy == LookupStrategy.SQL_QUERY) {
            return multiGetBySqlQuery(keys);
        }
        throw new RuntimeException("Unknown read strategy " + lookupStrategy);
    }

    private List<T> multiGeyByIds(List<List<Object>> keys) {
        List<Object> singleKeys = new ArrayList<Object>(keys.size());
        for (List<Object> key : keys) {
            singleKeys.add(toSingleKey(key));
        }
        ReadByIdsResult<T> result = space.readByIds(clazz, singleKeys.toArray());

        List<T> items = new ArrayList<T>(result.getResultsArray().length);
        Collections.addAll(items, result.getResultsArray());

        return items;
    }

    private List<T> multiGetBySqlQuery(List<List<Object>> keys) {
        List<T> items = new ArrayList<T>(keys.size());
        for (List<Object> key : keys) {
            sqlQuery.setParameters(key.toArray());
            T item = space.read(sqlQuery);
            items.add(item);
        }
        return items;
    }

    private Object toSingleKey(List<Object> key) {
        if (key.size() != 1) {
            throw new RuntimeException("XAP state does not support compound keys");
        }
        return key.get(0);
    }
}
