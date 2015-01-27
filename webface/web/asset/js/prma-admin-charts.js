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
$("#btn-load").click(function() {
    var minTime = $('#tq-minTime').val();
    var maxTime = $('#tq-maxTime').val();
    var lowLevel = $('#tq-lowLevel').val();
    var highLevel = $('#tq-highLevel').val();

    maxTime = maxTime ? new Date(maxTime).getTime() : new Date().getTime();
    minTime = minTime ? new Date(minTime).getTime() : maxTime - 86400000; // last day
    lowLevel = Prma.level2Int[lowLevel];
    highLevel = Prma.level2Int[highLevel];

    if (maxTime <= minTime) {
        alert("End time is earlier than start time");
        $('#tq-minTime').val("");
        $('#tq-maxTime').val("");
        return;
    }
    var interval = Math.floor((maxTime - minTime) / 48); // time interval for 48 points
    var q = {
        "minTime": minTime
        , "maxTime": maxTime
        , "lowLevel": lowLevel
        , "highLevel": highLevel
        , "interval": interval};

    //$.get("chart/statistics.json", q)
    $.get("testdata-charts.json", q)
        .done(function (obj) {
            //var obj = JSON.parse(resp);
            if (obj.code != 200) {
                alert(obj.message);
                return;
            }
            var data = obj.data;

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
            ,parseTime: false // ------------------ find a better way to parse time
            //,xLabelFormat: function(milli) {
            //    console.log(milli);
            //    return new Date(milli).toString()
            //}
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
                count: parseInt(raw._2)
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











