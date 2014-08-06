package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Displays the dynamic of visited pages for last N seconds.
 *
 * @author Oleksiy_Dyagilev
 */
public class PageViewTimeSeriesReport implements Serializable {

    private int windowLengthInSeconds;
    private int slotLengthInSeconds;
    private long[] counts;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageViewTimeSeriesReport that = (PageViewTimeSeriesReport) o;

        if (slotLengthInSeconds != that.slotLengthInSeconds) return false;
        if (windowLengthInSeconds != that.windowLengthInSeconds) return false;
        if (!Arrays.equals(counts, that.counts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = windowLengthInSeconds;
        result = 31 * result + slotLengthInSeconds;
        result = 31 * result + (counts != null ? Arrays.hashCode(counts) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PageViewTimeSeriesReport{" +
                ", windowLengthInSeconds=" + windowLengthInSeconds +
                ", slotLengthInSeconds=" + slotLengthInSeconds +
                ", counts=" + Arrays.toString(counts) +
                '}';
    }
}
