package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;

import java.io.Serializable;

/**
 * @author Mykola_Zalyayev
 */
@SpaceClass
public class OverallReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;
    private ActiveUsersReport activeUsersReport;
    private GeoReport geoReport;
    private PageViewTimeSeriesReport pageViewTimeSeriesReport;
    private TopReferralsReport topReferralsReport;
    private TopUrlsReport topUrlsReport;

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @SpaceIndex
    public ActiveUsersReport getActiveUsersReport() {
        return activeUsersReport;
    }

    public void setActiveUsersReport(ActiveUsersReport activeUsersReport) {
        this.activeUsersReport = activeUsersReport;
    }

    public GeoReport getGeoReport() {
        return geoReport;
    }

    public void setGeoReport(GeoReport geoReport) {
        this.geoReport = geoReport;
    }

    public PageViewTimeSeriesReport getPageViewTimeSeriesReport() {
        return pageViewTimeSeriesReport;
    }

    public void setPageViewTimeSeriesReport(PageViewTimeSeriesReport pageViewTimeSeriesReport) {
        this.pageViewTimeSeriesReport = pageViewTimeSeriesReport;
    }

    public TopReferralsReport getTopReferralsReport() {
        return topReferralsReport;
    }

    public void setTopReferralsReport(TopReferralsReport topReferralsReport) {
        this.topReferralsReport = topReferralsReport;
    }

    public TopUrlsReport getTopUrlsReport() {
        return topUrlsReport;
    }

    public void setTopUrlsReport(TopUrlsReport topUrlsReport) {
        this.topUrlsReport = topUrlsReport;
    }
}
