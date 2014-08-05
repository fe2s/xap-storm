var geoChart;

var geoOption = {};

var geoChartHasFocus = false;

function initGeoReport() {

    var block = document.getElementById('geo_div');
    var ratio = block.clientWidth / block.clientHeight;
    block.style.width = block.parentNode.clientWidth + 'px';
    block.style.height = block.clientWidth / ratio + 'px';
    geoChart = new google.visualization.GeoChart(block);

    $("#geo_div").mouseenter(function() {
        geoChartHasFocus = true;
    });
    $("#geo_div").mouseleave(function() {
        geoChartHasFocus = false;
    });
}

function drawGeoReport(data) {
    if(geoChartHasFocus){
        return;
    }
    var tableData = new google.visualization.DataTable();
    tableData.addColumn('string', 'Country');
    tableData.addColumn('number', 'Popularity');

    for(var key in data){
        var row = data[key]
        tableData.addRow([key, row]);
    }

    geoChart.draw(tableData, geoOption);
}