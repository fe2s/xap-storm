package com.gigaspaces.storm.googleanalytics.service;

import com.gigaspaces.storm.googleanalytics.model.OverallChartDataReport;
import com.gigaspaces.storm.googleanalytics.model.reports.*;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Mykola_Zalyayev
 */
@Service
public class ChartDataService {

    @Autowired
    private GigaSpace space;

    public ActiveUsersReport getCurrentVisitors() {
        return space.readById(ActiveUsersReport.class, 1L);
    }

    public TopReferralsReport getReferrals() {
        return space.readById(TopReferralsReport.class, 1L);
    }

    public TopUrlsReport getActivePage() {
        return space.readById(TopUrlsReport.class, 1L);
    }

    public PageViewTimeSeriesReport getRequestedPageCount() {
        return space.readById(PageViewTimeSeriesReport.class, 1L);
    }

    public GeoReport getGeoReport() {
        return space.readById(GeoReport.class, 1L);
    }

    public OverallChartDataReport getOverallChartDataReport() {
        OverallChartDataReport chartDataReport = new OverallChartDataReport();

        ActiveUsersReport usersReport = getCurrentVisitors();
        chartDataReport.setCurrentUserCount(usersReport != null ? usersReport.getActiveUsersNumber() : 0L);

        TopReferralsReport referralsReport = getReferrals();
        chartDataReport.setReferrals(referralsReport != null ? referralsReport.getTopReferrals() : new LinkedHashMap<String, Long>());

        TopUrlsReport urlsReport = getActivePage();
        chartDataReport.setActivePage(urlsReport != null ? urlsReport.getTopUrls() : new LinkedHashMap<String, Long>());

        PageViewTimeSeriesReport timeSeriesReport = getRequestedPageCount();

        chartDataReport.setSlotLengthInSeconds(timeSeriesReport != null ? timeSeriesReport.getSlotLengthInSeconds() : 1);
        chartDataReport.setWindowLengthInSeconds(timeSeriesReport != null ? timeSeriesReport.getWindowLengthInSeconds() : 60);
        chartDataReport.setPageCounts(timeSeriesReport != null ? timeSeriesReport.getCounts() : new long[0]);

        GeoReport geoReport = getGeoReport();
        chartDataReport.setGeoInfo(geoReport != null ? geoReport.getCountryCountMap() : new HashMap<String, Long>());

        return chartDataReport;
    }
}
