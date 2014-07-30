var referralsTable;

var referralsOption = {showRowNumber: true, is3D: true, title: 'Current page activities'};

var referralsHasFocus = false;

function drawReferrals() {

    var block = document.getElementById('referrals_table_div');
    referralsOption.width = block.clientWidth;
    referralsTable = new google.visualization.Table(block);

    $("#referrals_table_div").mouseenter(function() {
        referralsHasFocus = true;
    });
    $("#referrals_table_div").mouseleave(function() {
        referralsHasFocus = false;
    });
}

function successReferralsTable(data) {
    if(referralsHasFocus){
        return;
    }
    var tableData = new google.visualization.DataTable();
    tableData.addColumn('string', 'Source');
    tableData.addColumn('number', 'Active visitors');

    for(var key in data){
        var row = data[key];
        tableData.addRow([key,row]);
    }

    tableData.sort([{column: 1, desc:true}, {column: 0}])
    referralsTable.draw(tableData, referralsOption);
}