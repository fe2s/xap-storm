package com.gigaspaces.storm.googleanalytics.service;

import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.storm.googleanalytics.model.reports.OverallReport;
import com.gigaspaces.streaming.simple.SimpleStream;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
@Service
public class PageViewService {

    @Autowired
    private GigaSpace space;

    private SimpleStream<PageView> stream;

    @PostConstruct
    public void init() throws Exception {
        stream = new SimpleStream<PageView>(space, new PageView());
    }

    /**
     * sends page views to the stream
     */
    public void track(List<PageView> pageViewList) {
        if (!pageViewList.isEmpty()){
            stream.writeBatch(pageViewList);
        }
    }

    public void registerSite(String siteId) {
        OverallReport report = new OverallReport();
        report.setSiteId(siteId);
        space.write(report);
    }
}
