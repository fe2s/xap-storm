package com.gigaspaces.storm.trident.state;

import com.gigaspaces.client.ReadByIdsResult;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import org.openspaces.core.GigaSpace;
import storm.trident.state.OpaqueValue;
import storm.trident.state.TransactionalValue;
import storm.trident.state.map.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPState<T> implements IBackingMap<T> {

    private GigaSpace space;

    public XAPState(String spaceUrl) {
        this.space = GigaSpaceFactory.getInstance(spaceUrl);
    }

    @Override
    public List<T> multiGet(List<List<Object>> keys) {
        List<Object> singleKeys = new ArrayList<>(keys.size());
        for (List<Object> key : keys) {
            singleKeys.add(toSingleKey(key));
        }

        ReadByIdsResult<XAPStateItem> result = space.readByIds(XAPStateItem.class, singleKeys.toArray());

        List<T> items = new ArrayList<T>(singleKeys.size());

        for (XAPStateItem item : result.getResultsArray()) {
            if (item != null) {
                Serializable xapValue = item.getValue();
                T val = (T) convertFromXapValue(xapValue);
                items.add(val);
            } else {
                items.add(null);
            }
        }
        return items;
    }

    @Override
    public void multiPut(List<List<Object>> keys, List<T> vals) {
        List<XAPStateItem> items = new ArrayList<>(keys.size());

        for (int i = 0; i < keys.size(); i++) {
            Object key = toSingleKey(keys.get(i));
            T val = vals.get(i);
            Serializable xapVal = convertToXapValue(val);
            items.add(new XAPStateItem<>(key, xapVal));
        }
        space.writeMultiple(items.toArray(), WriteModifiers.ONE_WAY);
    }

    private Serializable convertToXapValue(T val) {
        if (val instanceof Serializable) {
            return (Serializable) val;
        }
        if (val instanceof TransactionalValue) {
            TransactionalValue<T> transactional = (TransactionalValue) val;
            return new XAPTransactionalValue<T>(transactional.getTxid(), transactional.getVal());
        }
        if (val instanceof OpaqueValue) {
            OpaqueValue opaqueValue = (OpaqueValue) val;
            return new XAPOpaqueValue<>(opaqueValue.getCurrTxid(), opaqueValue.getCurr(), opaqueValue.getPrev());
        }
        throw new RuntimeException("Non serializable and not supported value " + val);
    }

    private Object convertFromXapValue(Object xapVal) {
        if (xapVal instanceof XAPTransactionalValue) {
            XAPTransactionalValue xapTransactional = (XAPTransactionalValue) xapVal;
            return new TransactionalValue<>(xapTransactional.getTxid(), xapTransactional.getVal());
        }
        if (xapVal instanceof XAPOpaqueValue) {
            XAPOpaqueValue xapOpaque = (XAPOpaqueValue) xapVal;
            return new OpaqueValue<>(xapOpaque.getCurrTxid(), ((XAPOpaqueValue) xapVal).getCurr(), xapOpaque.getPrev());
        }
        return xapVal;
    }

    private Object toSingleKey(List<Object> key) {
        if (key.size() != 1) {
            throw new RuntimeException("XAP state does not support compound keys");
        }
        return key.get(0);
    }
}
