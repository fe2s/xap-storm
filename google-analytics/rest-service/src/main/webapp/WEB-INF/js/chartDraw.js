google.load('visualization', '1.0', {'packages':['corechart','table','geochart']});

google.setOnLoadCallback(drawCharts);

function drawCharts(){

    initTopUrlsReport();
    initGeoReport();
    initPageViewTimeSeries();
    initTopReferralsReport();


    drawChartsAJAX()

    setInterval(drawChartsAJAX, 1000)
}

function drawChartsAJAX(){
    $.get("overallChartsData",successCharts, "json");
}

function successCharts(data) {

    drawTopUrlsReport(data.topUrlsReport.topUrls);
    drawActiveUsersReport(data.activeUsersReport.activeUsersNumber);
    drawGeoReport(data.geoReport.countryCountMap);
    drawPageViewTimeSeries(data.pageViewTimeSeriesReport);
    drawTopReferralsReport(data.topReferralsReport.topReferrals);
}