package com.gigaspaces.storm.googleanalytics.service;

import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.streaming.simple.SimpleStream;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
@Service
public class PageViewService implements InitializingBean {

    @Autowired
    private GigaSpace space;

    private SimpleStream<PageView> stream;

    @Override
    public void afterPropertiesSet() throws Exception {
        stream = new SimpleStream<>(space, new PageView());
    }

    public void track(List<PageView> pageViewList) {
        stream.writeBatch(pageViewList);
    }

}
