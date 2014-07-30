package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class TopUrlsReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;
    private LinkedHashMap<String, Long> topUrls;

    public TopUrlsReport() {
    }

    public TopUrlsReport(LinkedHashMap<String, Long> topUrls) {
        this.topUrls = topUrls;
    }

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LinkedHashMap<String, Long> getTopUrls() {
        return topUrls;
    }

    public void setTopUrls(LinkedHashMap<String, Long> topUrls) {
        this.topUrls = topUrls;
    }
}
