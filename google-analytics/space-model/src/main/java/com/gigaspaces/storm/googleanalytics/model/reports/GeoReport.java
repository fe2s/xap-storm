package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.Map;

/**
 * Shows users distribution across countries.
 *
 * @author Oleksiy_Dyagilev
 */
public class GeoReport implements Serializable {

    private Map<String, Long> countryCountMap;

    public Map<String, Long> getCountryCountMap() {
        return countryCountMap;
    }

    public void setCountryCountMap(Map<String, Long> countryCountMap) {
        this.countryCountMap = countryCountMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoReport report = (GeoReport) o;

        if (countryCountMap != null ? !countryCountMap.equals(report.countryCountMap) : report.countryCountMap != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return countryCountMap != null ? countryCountMap.hashCode() : 0;
    }
}
