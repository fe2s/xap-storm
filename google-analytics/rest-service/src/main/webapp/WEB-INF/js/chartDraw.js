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

    successActivePageChart(data.activePage);
    successVisitors(data.currentUserCount);
    successGeoChart(data.geoInfo);
    requestedPageData = {windowLengthInSeconds: data.windowLengthInSeconds,
                         slotLengthInSeconds: data.slotLengthInSeconds, counts: data.pageCounts}
    successRequestedPageChart(requestedPageData);
    successReferralsTable(data.referrals);
}