package com.gigaspaces.storm.trident.state;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPOpaqueValue<T> implements Serializable {

    private Long currTxid;
    private T prev;
    private T curr;

    public XAPOpaqueValue(Long currTxid, T val, T prev) {
        this.curr = val;
        this.currTxid = currTxid;
        this.prev = prev;
    }

    public Long getCurrTxid() {
        return currTxid;
    }

    public void setCurrTxid(Long currTxid) {
        this.currTxid = currTxid;
    }

    public T getPrev() {
        return prev;
    }

    public void setPrev(T prev) {
        this.prev = prev;
    }

    public T getCurr() {
        return curr;
    }

    public void setCurr(T curr) {
        this.curr = curr;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
