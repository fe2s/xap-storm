package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class PageViewTimeSeriesReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;

    private int windowLengthInSeconds;
    private int slotLengthInSeconds;
    private long[] counts;

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWindowLengthInSeconds() {
        return windowLengthInSeconds;
    }

    public void setWindowLengthInSeconds(int windowLengthInSeconds) {
        this.windowLengthInSeconds = windowLengthInSeconds;
    }

    public int getSlotLengthInSeconds() {
        return slotLengthInSeconds;
    }

    public void setSlotLengthInSeconds(int slotLengthInSeconds) {
        this.slotLengthInSeconds = slotLengthInSeconds;
    }

    public long[] getCounts() {
        return counts;
    }

    public void setCounts(long[] counts) {
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "PageViewTimeSeriesReport{" +
                "id=" + id +
                ", windowLengthInSeconds=" + windowLengthInSeconds +
                ", slotLengthInSeconds=" + slotLengthInSeconds +
                ", counts=" + Arrays.toString(counts) +
                '}';
    }
}
