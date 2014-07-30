package com.gigaspaces.storm.googleanalytics.controller;

import com.gigaspaces.storm.googleanalytics.model.OverallChartDataReport;
import com.gigaspaces.storm.googleanalytics.model.reports.*;
import com.gigaspaces.storm.googleanalytics.service.ChartDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mykola_Zalyayev
 */
@Controller
public class ChartDataController {

    @Autowired
    private ChartDataService service;

    @RequestMapping(value = "/currentVisitors", method = RequestMethod.GET)
    @ResponseBody
    public Long getCurrentVisitors() {
        ActiveUsersReport usersReport = service.getCurrentVisitors();

        return usersReport != null ? usersReport.getActiveUsersNumber() : 0L;
    }

    @RequestMapping(value = "/referrals", method = RequestMethod.GET)
    @ResponseBody
    public LinkedHashMap<String, Long> getReferrals() {
        TopReferralsReport referralsReport = service.getReferrals();
        return referralsReport != null ? referralsReport.getTopReferrals() : new LinkedHashMap<String, Long>();
    }

    @RequestMapping(value = "/activePage", method = RequestMethod.GET)
    @ResponseBody
    public LinkedHashMap<String, Long> getActivePage() {
        TopUrlsReport urlsReport = service.getActivePage();
        return urlsReport != null ? urlsReport.getTopUrls() : new LinkedHashMap<String, Long>();
    }

    @RequestMapping(value = "/requestedPageCount", method = RequestMethod.GET)
    @ResponseBody
    public PageViewTimeSeriesReport getRequestedPageCount() {
        PageViewTimeSeriesReport report = service.getRequestedPageCount();
        return report != null ? report : new PageViewTimeSeriesReport();
    }

    @RequestMapping(value = "/geoLocationCount", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Long> geoReport() {
        GeoReport report = service.getGeoReport();
        return report != null ? report.getCountryCountMap() : new HashMap<String, Long>();
    }

    @RequestMapping(value = "/overallChartsData", method = RequestMethod.GET)
    @ResponseBody
    public OverallChartDataReport getOverallChartDataReport() {
        return service.getOverallChartDataReport();
    }
}
