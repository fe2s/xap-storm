package com.gigaspaces.storm.trident.state;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import storm.trident.state.TransactionalValue;

import java.io.*;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class XAPStateItem<T extends Serializable> {
    private Object key;
    private T value;

    public XAPStateItem() {
    }

    public XAPStateItem(Object key, T value) {
        this.key = key;
        this.value = value;
    }

    @SpaceId(autoGenerate = false)
    @SpaceRouting
    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
