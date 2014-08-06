package com.gigaspaces.storm.googleanalytics.controller;

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
 * Endpoint to retrieve analytic reports.
 * Called from browser (AJAX).
 *
 * @author Mykola_Zalyayev
 */
@Controller
public class ChartDataController {

    @Autowired
    private ChartDataService service;

    @RequestMapping(value = "/overallChartsData", method = RequestMethod.GET)
    @ResponseBody
    public OverallReport getOverallChartDataReport() {
        return service.getOverallChartDataReport();
    }
}
