package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class GeoReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;
    private Map<String, Long> countryCountMap;

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Long> getCountryCountMap() {
        return countryCountMap;
    }

    public void setCountryCountMap(Map<String, Long> countryCountMap) {
        this.countryCountMap = countryCountMap;
    }
}
