package com.gigaspaces.storm.googleanalytics.controller;


import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.storm.googleanalytics.service.PageViewService;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
@Controller
@RequestMapping(value = "/rest")
public class AnalyticsEndpointController {

    @Autowired
    private GigaSpace space;

    @Autowired
    private PageViewService pageViewService;

    @RequestMapping(value = "/trackPageViewList", method = RequestMethod.POST)
    @ResponseBody
    public void trackPageViewList(@RequestBody List<PageView> pageViewList) {
        pageViewService.track(pageViewList);
    }
}
