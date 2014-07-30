var activePageTable;

var activePageOption = {allowHtml: true, showRowNumber: true, is3D: true, title: 'Current page activities'};

var activePageHasFocus = false;

function drawActivePage() {

	var block = document.getElementById('active_page_table_div');
    activePageOption.width = block.clientWidth;
    activePageTable = new google.visualization.Table(block);

    $("#active_page_table_div").mouseenter(function() {
        activePageHasFocus = true;
    });
    $("#active_page_table_div").mouseleave(function() {
        activePageHasFocus = false;
    });
}

function successActivePageChart(data) {
    if(activePageHasFocus){
        return;
    }

    var tableData = new google.visualization.DataTable();
    tableData.addColumn('string', 'Active page');
    tableData.addColumn('number', '');
    tableData.addColumn('number', 'Active visitors %');

    var pageVisitorsCount = 0;
    for(var key in data){
        var row = data[key];
        pageVisitorsCount+= row;
    }

    for(var key in data){
        var row = data[key];
        var pagePercent = row/pageVisitorsCount*100;
        pagePercent = parseFloat(pagePercent.toFixed(2))
        tableData.addRow([key,row, pagePercent]);
    }

    var barFormatter = new google.visualization.BarFormat({width: 120, min: 0});
    barFormatter.format(tableData, 2); // Apply formatter to second column

    tableData.sort([{column: 1, desc:true}, {column: 0}])
    activePageTable.draw(tableData, activePageOption);
}