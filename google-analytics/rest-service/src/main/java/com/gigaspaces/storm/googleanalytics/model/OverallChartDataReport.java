package com.gigaspaces.storm.googleanalytics.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mykola_Zalyayev
 */
public class OverallChartDataReport {

    private Long currentUserCount;
    private LinkedHashMap<String, Long> referrals;
    private LinkedHashMap<String, Long> activePage;
    private int windowLengthInSeconds;
    private int slotLengthInSeconds;
    private long[] pageCounts;
    private Map<String, Long> geoInfo;

    public Long getCurrentUserCount() {
        return currentUserCount;
    }

    public void setCurrentUserCount(Long currentUserCount) {
        this.currentUserCount = currentUserCount;
    }

    public LinkedHashMap<String, Long> getReferrals() {
        return referrals;
    }

    public void setReferrals(LinkedHashMap<String, Long> referrals) {
        this.referrals = referrals;
    }

    public LinkedHashMap<String, Long> getActivePage() {
        return activePage;
    }

    public void setActivePage(LinkedHashMap<String, Long> activePage) {
        this.activePage = activePage;
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

    public long[] getPageCounts() {
        return pageCounts;
    }

    public void setPageCounts(long[] pageCounts) {
        this.pageCounts = pageCounts;
    }

    public Map<String, Long> getGeoInfo() {
        return geoInfo;
    }

    public void setGeoInfo(Map<String, Long> geoInfo) {
        this.geoInfo = geoInfo;
    }
}
