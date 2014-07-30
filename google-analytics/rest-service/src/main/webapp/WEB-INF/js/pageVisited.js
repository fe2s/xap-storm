var pageVisitsChart;

var pageVisitsOptions = {title:"", width:600, height:400,
                hAxis: {direction: -1, viewWindow:{max:30.5, min: -0.5}},
                vAxis: {maxValue:30.5},
                animation: {duration:1000,easing:'out'},
                bar: {groupWidth:'100%'}, isStacked: true,
                legend: { position: "none" }}


function drawRequestedPage() {

    var block = document.getElementById('columnchart_values');
    var ratio = pageVisitsOptions.width / pageVisitsOptions.height;
    pageVisitsChart = new google.visualization.ColumnChart(block);
    pageVisitsOptions.width = block.clientWidth;
    pageVisitsOptions.height = pageVisitsOptions.width / ratio;
}

function successRequestedPageChart(data) {
    var tableData = new google.visualization.DataTable();
    // Create and populate the data table.
    tableData.addColumn('number', 'Time');
    tableData.addColumn('number', 'Count');
    tableData.addColumn({type:'string', role: 'style'});
    tableData.addColumn('number', 'Count');
    tableData.addColumn({type:'string', role: 'style'});
    var style ='fill-opacity: 0.4'

    pageVisitsOptions.hAxis.viewWindow.max = data.windowLengthInSeconds;
    var slotLength = data.slotLengthInSeconds

    var index = 0.5;
    var maxCount = 0;
    for(var i=0;i<data.counts.length; i++){
        var row = data.counts[i];
        if(row>maxCount){
            maxCount = row;
        }
        for(var j = 0;j<slotLength;j++){
            tableData.addRow([index++,row,style,row/100,'color: blue;']);
        }
    }

    pageVisitsOptions.vAxis.maxValue = maxCount+maxCount*0.1;


    // Create and draw the visualization.
    pageVisitsChart.draw(tableData, pageVisitsOptions);
}