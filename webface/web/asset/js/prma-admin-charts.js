/**
 * Created by Bowen Cai on 1/5/2015.
 */
$(".form_datetime").datetimepicker({
    format: 'yyyy-mm-dd hh:ii:ss',
    todayHighlight: true,
    todayBtn: true,
    minuteStep: 1,
    autoclose: true
});
$("#btn-load").click(function(){
    var minTime = $('#tq-minTime').val();
    var maxTime = $('#tq-maxTime').val();
    $.get("testdata-charts.txt", {minTime: minTime, maxTime: maxTime})
        .done(function (resp) {
            var data = JSON.parse(resp).data;

            PrmaCharts.showTable(data._2);

            Morris.Line(PrmaCharts.timeline(data._1));
            Morris.Donut(PrmaCharts.levelCount(data._2));
            Morris.Bar(PrmaCharts.loggerCount(data._3));
        });
});



var PrmaCharts = {

    timeline: function(arr) {
        var timeArray = [];
        for(var i = 0; i < arr.length; i++) {
            var raw = arr[i];
            var count = {x: raw._6 + ''
                ,trace: raw._1
                ,debug: raw._2
                ,info: raw._3
                ,warn: raw._4
                ,error: raw._5};
            timeArray.push(count);
        }
        var names = ["trace", "debug", "info", "warn", "error"];
        return {
            element: "log-timeline"
            ,data: timeArray
            ,xkey: "x"
            ,ykeys: names
            ,labels:names
            ,parseTime: false // ------------------ fnd a better way to parse time
        }
    }

    , levelCount: function(arr) {
        return {
            element: "log-level-count"
            ,data: [
                {label: "Trace", value: arr[0]}
                ,{label: "Debug", value: arr[1]}
                ,{label: "Info", value: arr[2]}
                ,{label: "Warn", value: arr[3]}
                ,{label: "Error", value: arr[4]}
            ]
        }
    }

    , loggerCount: function(arr) {
        var loggerArr = [];
        for (var i = 0; i < arr.length; i++) {
            var raw = arr[i];
            var one = {
                name: raw._1,
                count: raw._2
            };
            loggerArr.push(one);
        }
        return {
            element: "log-logger-count"
            ,data: loggerArr
            ,xkey: "name"
            ,ykeys:["count"]
            ,labels:["Logger Name"]
        };
    }

    , showTable: function(arr) {
        var sum = arr.reduce(function(pv, cv) { return pv + cv; }, 0);
        var template = '<tr><td class="col-lg-4">Trace Count:</td><td>' + arr[0] + '</td></tr>'+
            '<tr class="success"><td>Debug Count:</td><td>' + arr[1] + '</td></tr>'+
            '<tr class="info"><td>Info Count:</td><td>' + arr[2] + '</td></tr>'+
            '<tr class="warning"><td>Warn Count:</td><td>' + arr[3] + '</td></tr>'+
            '<tr class="danger"><td>Error Count:</td><td>' + arr[4] + '</td></tr>'+
            '<tr class="top-buffer"><td></td><td></td></tr>'+
            '<tr class="active"><td>Total Log  Count:</td><td>' + sum + '</td></tr>';
        $("#table-summay").html(template);
    }
}











