package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Shows top visited urls for the last N seconds.
 *
 * @author Oleksiy_Dyagilev
 */
public class TopUrlsReport implements Serializable {

    private LinkedHashMap<String, Long> topUrls;

    public TopUrlsReport() {
    }

    public TopUrlsReport(LinkedHashMap<String, Long> topUrls) {
        this.topUrls = topUrls;
    }

    public LinkedHashMap<String, Long> getTopUrls() {
        return topUrls;
    }

    public void setTopUrls(LinkedHashMap<String, Long> topUrls) {
        this.topUrls = topUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopUrlsReport that = (TopUrlsReport) o;

        if (topUrls != null ? !topUrls.equals(that.topUrls) : that.topUrls != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return topUrls != null ? topUrls.hashCode() : 0;
    }
}
