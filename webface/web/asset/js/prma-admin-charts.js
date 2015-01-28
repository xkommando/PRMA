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

            Morris.Line(PrmaCharts.timeline(data._1));
            PrmaCharts.showTable(data._2);

            Morris.Donut(PrmaCharts.levelCount(data._2));

            $('#log-logger-count').highcharts(PrmaCharts.loggerCount(data._3));
            $('#log-logger-heat').highcharts(PrmaCharts.loggerLevelHeatMap(data._3));

            $('#log-exception-count').highcharts(PrmaCharts.exceptCount(data._4));

            //Morris.Bar(PrmaCharts.loggerCount(data._3));
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
        //{
        //    "_1": "logger1",
        //    "_2": [10, 20, 30, 40, 199, 299]
        //},

        var loggerNameArr = [];

        var traceCounts = [];
        var infoCounts = [];
        var debugCounts = [];
        var warnCounts = [];
        var errorCounts = [];

        for(var i = 0; i < arr.length; i++) {
            var raw = arr[i];

            var counts = raw._2;
            var loggerName = raw._1 + ' (' + counts[5] + ')';
            //var logCounts = counts.slice(0, 5);

            loggerNameArr.push(loggerName);

            traceCounts.push(counts[0]);
            infoCounts.push(counts[1]);
            debugCounts.push(counts[2]);
            warnCounts.push(counts[3]);
            errorCounts.push(counts[4]);
        }
        return {
            chart: {
                type: 'bar'
            },
            title: {
                text: 'Log counts by different loggers'
            },
            xAxis: {
                categories: loggerNameArr,
                    title: {
                    text: "Highest " + arr.length + " Loggers (descending order)"
                }
            },
            yAxis: {
                min: 0,
                    title: {
                    text: 'count',
                        align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            },
            legend: {
                layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: -40,
                    y: 100,
                    floating: true,
                    borderWidth: 1,
                    //backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
                    shadow: true
            },
            credits: {
                enabled: false
            },
            series: [{
                name: 'error',
                data: errorCounts
            }, {
                name: 'warn',
                data: warnCounts
            }, {
                name: 'info',
                data: infoCounts
            }, {
                name: 'debug',
                data: debugCounts
            },{
                name: 'trace',
                data: traceCounts
            }]
        }
    }

    , exceptCount: function(arr) {
        //{
        //    "_1": "java.lang.RuntimeException",
        //    "_2": 999
        //},
        var exceptNameArr = [];
        var exceptCountArr = [];

        for(var i = 0; i < arr.length; i++) {
            var raw = arr[i];
            exceptNameArr.push(raw._1);
            exceptCountArr.push(raw._2);
        }

        return {
            chart: {
                type: 'bar'
            },
            title: {
                text: 'Exception count'
            },
            xAxis: {
                categories: exceptNameArr,
                title: {
                    text: "Hottest " + arr.length + " Exceptions (descending order)"
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'count',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -40,
                y: 100,
                floating: true,
                borderWidth: 1,
                //backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
                shadow: true
            },
            credits: {
                enabled: false
            },
            series: [{
                name: 'count',
                data: exceptCountArr
            }]
        }
    }

    , loggerLevelHeatMap: function (arr) {
        var coordData = [];
        var loggerNames = [];
        for (var i = 0; i < arr.length; i++) {
            var raw = arr[i];
            loggerNames.push(raw._1);
            var counts = raw._2;
            coordData.push([0, i, counts[0]]);
            coordData.push([1, i, counts[1]]);
            coordData.push([2, i, counts[2]]);
            coordData.push([3, i, counts[3]]);
            coordData.push([4, i, counts[4]]);
            coordData.push([5, i, counts[5]]);
        }
        return {
            chart: {
                type: 'heatmap',
                marginTop: 40,
                marginBottom: 40
            },
            title: {
                text: 'Logs per logger per level'
            },
            xAxis: {
                categories: ['trace', 'debug', 'info', 'warn', 'error', 'total']
            },
            yAxis: {
                categories:  loggerNames,
                reversed: true,
                title: null
            },
            colorAxis: {
                min: 0,
                minColor: '#FFFFFF',
                maxColor: Highcharts.getOptions().colors[0]
            },

            legend: {
                align: 'right',
                layout: 'vertical',
                margin: 0,
                verticalAlign: 'top',
                y: 25,
                symbolHeight: 320
            },

            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> logged <br><b>' +
                        this.point.value + '</b> event on <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
                }
            },

            series: [{
                name: 'logs per logger',
                borderWidth: 1,
                data: coordData,
                dataLabels: {
                    enabled: true,
                    color: 'black',
                    style: {
                        textShadow: 'none',
                        HcTextStroke: null
                    }
                }
            }]

        };

    }
    //, loggerCount: function(arr) {
    //    var loggerArr = [];
    //    for (var i = 0; i < arr.length; i++) {
    //        var raw = arr[i];
    //        var one = {
    //            name: raw._1,
    //            count: parseInt(raw._2)
    //        };
    //        loggerArr.push(one);
    //    }
    //    return {
    //        element: "log-logger-count"
    //        ,data: loggerArr
    //        ,xkey: "name"
    //        ,ykeys:["count"]
    //        ,labels:["Logger Name"]
    //    };
    //}

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











