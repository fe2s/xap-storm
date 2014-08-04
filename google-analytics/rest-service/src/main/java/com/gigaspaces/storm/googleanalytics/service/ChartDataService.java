package com.gigaspaces.storm.googleanalytics.service;

import com.gigaspaces.storm.googleanalytics.model.reports.*;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mykola_Zalyayev
 */
@Service
public class ChartDataService {

    @Autowired
    private GigaSpace space;

    public ActiveUsersReport getCurrentVisitors() {
        SQLQuery<OverallReport> query = new SQLQuery<>(OverallReport.class,"id = 1").setProjections("activeUsersReport");
        return space.read(query).getActiveUsersReport();
    }

    public TopReferralsReport getReferrals() {

        SQLQuery<OverallReport> query = new SQLQuery<>(OverallReport.class,"id = 1").setProjections("topReferralsReport");
        return space.read(query).getTopReferralsReport();
    }

    public TopUrlsReport getActivePage() {
        SQLQuery<OverallReport> query = new SQLQuery<>(OverallReport.class,"id = 1").setProjections("topUrlsReport");
        return space.read(query).getTopUrlsReport();
    }

    public PageViewTimeSeriesReport getRequestedPageCount() {
        SQLQuery<OverallReport> query = new SQLQuery<>(OverallReport.class,"id = 1").setProjections("pageViewTimeSeriesReport");
        return space.read(query).getPageViewTimeSeriesReport();
    }

    public GeoReport getGeoReport() {
        SQLQuery<OverallReport> query = new SQLQuery<>(OverallReport.class,"id = 1").setProjections("geoReport");
        return space.read(query).getGeoReport();
    }

    // TODO: optimize model to get all reports with 1 API call
    public OverallReport getOverallChartDataReport() {
        return space.readById(OverallReport.class, 1L);
    }
}
