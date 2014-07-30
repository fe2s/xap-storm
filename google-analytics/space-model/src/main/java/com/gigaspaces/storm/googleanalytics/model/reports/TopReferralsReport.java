package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class TopReferralsReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;
    private LinkedHashMap<String, Long> topReferrals;

    public TopReferralsReport() {
    }

    public TopReferralsReport(LinkedHashMap<String, Long> topReferrals) {
        this.topReferrals = topReferrals;
    }

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LinkedHashMap<String, Long> getTopReferrals() {
        return topReferrals;
    }

    public void setTopReferrals(LinkedHashMap<String, Long> topReferrals) {
        this.topReferrals = topReferrals;
    }
}
