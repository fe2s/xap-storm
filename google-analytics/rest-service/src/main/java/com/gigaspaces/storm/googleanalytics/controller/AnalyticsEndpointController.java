package com.gigaspaces.storm.googleanalytics.controller;


import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.storm.googleanalytics.service.PageViewService;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint to send PageView json documents.
 * Used by 'feeder' app.
 *
 * @author Mykola_Zalyayev
 */
@Controller
@RequestMapping(value = "/rest")
public class AnalyticsEndpointController {

    @Autowired
    private PageViewService pageViewService;

    @RequestMapping(value = "/register", method = RequestMethod.PUT)
    @ResponseBody
    public void registerSite(@RequestBody String siteId){
        pageViewService.registerSite(siteId);
    }

    @RequestMapping(value = "/trackPageViewList", method = RequestMethod.POST)
    @ResponseBody
    public void trackPageViewList(@RequestBody List<PageView> pageViewList) {
        pageViewService.track(pageViewList);
    }
}
