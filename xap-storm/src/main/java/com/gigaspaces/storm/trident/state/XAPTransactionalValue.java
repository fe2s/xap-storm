package com.gigaspaces.storm.trident.state;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPTransactionalValue<T> implements Serializable {
    private T val;
    private Long txid;

    public XAPTransactionalValue(Long txid, T val) {
        this.val = val;
        this.txid = txid;
    }

    public T getVal() {
        return val;
    }

    public Long getTxid() {
        return txid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
