google.load('visualization', '1.0', {'packages':['corechart','table','geochart']});

google.setOnLoadCallback(drawCharts);

function drawCharts(){

    drawActivePage();
    drawGeo();
    drawRequestedPage();
    drawReferrals();


    drawChartsAJAX()

    setInterval(drawChartsAJAX, 1000)
}

function drawChartsAJAX(){
    $.get("overallChartsData",successCharts, "json");
}

function successCharts(data) {

    successActivePageChart(data.topUrlsReport.topUrls);
    successVisitors(data.activeUsersReport.activeUsersNumber);
    successGeoChart(data.geoReport.countryCountMap);
    successRequestedPageChart(data.pageViewTimeSeriesReport);
    successReferralsTable(data.topReferralsReport.topReferrals);
}