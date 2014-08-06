package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Shows top referrals for the last N seconds.
 *
 * @author Oleksiy_Dyagilev
 */
public class TopReferralsReport implements Serializable {

    private LinkedHashMap<String, Long> topReferrals;

    public TopReferralsReport() {
    }

    public TopReferralsReport(LinkedHashMap<String, Long> topReferrals) {
        this.topReferrals = topReferrals;
    }

    public LinkedHashMap<String, Long> getTopReferrals() {
        return topReferrals;
    }

    public void setTopReferrals(LinkedHashMap<String, Long> topReferrals) {
        this.topReferrals = topReferrals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopReferralsReport that = (TopReferralsReport) o;

        if (topReferrals != null ? !topReferrals.equals(that.topReferrals) : that.topReferrals != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return topReferrals != null ? topReferrals.hashCode() : 0;
    }
}
