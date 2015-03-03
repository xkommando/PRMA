/**
 * Created by Bowen Cai on 12/25/2014.
 */


$(".form_datetime").datetimepicker({
    format: 'yyyy-mm-dd hh:ii:ss',
    todayHighlight: true,
    todayBtn: true,
    minuteStep: 1,
    autoclose: true
});

$("#tq-msg-fuzzy").bootstrapSwitch();
$("#tq-only-except").bootstrapSwitch();


// cached data
var logTableElem = $('#prma-log-table');
var dtable = {};
$(document).ready(function () {

    //var dtable = logTable.dataTable();
    dtable = logTableElem.DataTable(PrmaLog.dtableOptions);

    setTimeout(PrmaLog.update3D, 1000);

    dtable.on( 'draw', function () {
        $('.dt-level').each(function (i, e) {
            switch (e.innerHTML){
                case "INFO":
                    e.classList.add("info");
                    break;
                case "FATAL":
                case "ERROR":
                    e.classList.add("danger");
                    break;
                case "WARN":
                    e.classList.add("warning");
                    break;
                case "DEBUG":
                    e.classList.add("success");
                    break;
            }
        });
    } );

    // Add event listener for opening and closing details
    $('#prma-log-table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = dtable.row( tr );

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            var data = row.data();
            var evId = data.id;
            var evLoggerN = data.loggerName;
            var evthreadN = data.threadName;
            var evFlag = data.flag;
            var qp = {id: evId, flag: evFlag, loggerName: evLoggerN, threadName: evthreadN}
            //$.get("log/detail.json", qp)
            $.get("testdata-detail.txt", qp)
                .done(function (resp) {
                    var obj = JSON.parse(resp); // -------------
                    if (obj.code != 200) {
                        alert(obj.message);
                        return;
                    }
                    var data = obj.data;
                    var htmlStr = PrmaLog.logDetail(data);
                    row.child(htmlStr).show();
                    tr.addClass('shown');
                });
            setTimeout(PrmaLog.update3D, 1000);
        }
    } );

    console.log("fk11");

});

$('#tq-btn').click(function () {
    var psql = $("#log-p-sql").val();
    var query = "";
    if (psql && psql.length > 0) {
        query = "p-sql=" + encodeURIComponent(psql);
    } else {
        var minTime = $('#tq-minTime').val();
        var maxTime = $('#tq-maxTime').val();
        var llevel = $('#tq-llevel').val();
        var hlevel = $('#tq-hlevel').val();
        var lgName = $('#tq-logger').val();
        var threadName = $('#tq-thread').val();
        var msg = $('#tq-msg').val();
        var exceptOnly = $("#tq-only-except").prop("checked");
        var msgFuzzyQ = $("#tq-msg-fuzzy").prop("checked");

        maxTime = maxTime ? new Date(maxTime).getTime() : new Date().getTime();
        minTime = minTime ? new Date(minTime).getTime() : maxTime - 3600000; // last hour
        if (maxTime <= minTime) {
            alert("End time is earlier than start time");
            $('#tq-minTime').val("");
            $('#tq-maxTime').val("");
            return;
        }
        query = {
            "minTime": minTime,
            "maxTime": maxTime,
            "lowLevel": llevel,
            "highLevel": hlevel,
            "exceptionOnly": exceptOnly
        };
        if (lgName)
            query["loggerName"] = lgName;
        if (threadName)
            query["threadName"] = threadName;
        if (msg) {
            query["message"] = msg;
            query["fuzzyQuery"] = msgFuzzyQ;
        }
        query = Prma.encodeQueryData(query);
        console.log(query);
    }

    dtable.ajax.url("ajax/log/list.json?" + query).load();

    //$.get( "http://localhost:63342/PRMA/webface/src/main/webapp", q)
    //    .done(function( data ) {
    //        alert( "Data Loaded: " + data );
    //    });
});


var PrmaLog = {
    dtableOptions: {
        //"ajax": "testdata-list.txt?minTime=1&maxTime=9223372036854775807&lowLevel=TRACE&highLevel=FATAL&exceptionOnly=false",
        "ajax": "ajax/log/list.json?minTime=1&maxTime=9223372036854775807&lowLevel=TRACE&highLevel=FATAL&exceptionOnly=false",
        "columns": [
            {
                "data": "timeCreated",
                "width": "11%",
                "render": function (data, type, row) {
                    return Prma.fmtDate(row.timeCreated);
                }
            }, // time
            {
                "data": "level"
                ,"width": "2%"
                ,"className": "dt-level"
            }, // level
            {
                "data": "callerStackTrace"
                ,"width": "15%"
                , "render": function (data, type, row) {
                return Prma.fmtLocation(data, row.loggerName);
            }
            }, // logger
            {
                "data": "message",
                "width": "62%"
            }, // message
            {
                "data": "threadName",
                "width": "8%"
            }, // message
            {
                "width": "2%",
                "className": 'details-control',
                "orderable": false,
                "data": null,
                "defaultContent": ''
            }
        ]
        //responsive: true,
        //"jQueryUI": true
    }

    , logDetail: function(data) {
        var callerStr = Prma.fmtStackTrace(data.callerStackTrace);

        var tagStr = "";
        if (data.tags) {
            tagStr = ' <span class="label label-info">';
            tagStr = tagStr.concat(data.tags.join('</span> <span class="label label-info"> '));
            tagStr = tagStr.concat("</span> ");
        }

        var propStr = "";
        if (data.properties) {
            var _props = data.properties;
            var _p = [];
            for(var _k in _props)
                _p.push("<tr><td>" + _k + "</td><td>" + _props[_k] + "</td></tr>");
            propStr = _p.join("\r\n");
        }

        var exceptStr = "<tr>";
        if (data.exceptions) {

            var exceptions = data.exceptions;
            var _lsExcept = [];

            for (var i = 0; i < exceptions.length; i++) {
                var except = exceptions[i];

                var head = '<td> <a href="/inspect/except?ins2Except=' + encodeURI(except.name)  + '" target="_blank">' + except.name + '</a><hr/><label class="label label-info">' + except.message + '</label></td>';

                var stacks = "<td style='text-decoration: underline;'>";
                if (except.stackTraces) {
                    var _sts = except.stackTraces;
                    var lines = [];

                    for (var _i = 0; _i < _sts.length; _i++)
                        lines.push(Prma.fmtStackTrace(_sts[_i]));

                    stacks = stacks.concat(lines.join("\r\n<br/>"));
                }
                stacks = stacks.concat("</td>");
                _lsExcept.push(head.concat(stacks))
            }

            exceptStr = exceptStr.concat(_lsExcept.join("</tr>\r\n<tr>"))
        }
        exceptStr = exceptStr.concat("</tr>");

        //console.log(exceptStr);

        return '<table class="table">'
            +'    <tbody>'
            +'        <tr>'
            +'            <td> <label> ID: </label> ' + data.id
            +'            </td>'
            +'            <td> <label> Caller: </label> ' + callerStr
            +'            </td>'
            +'            <td> Reserved: <span class="badge">' + data.reserved + '</span>'
            +'            </td>'
            +'        </tr>'
            +'        <tr>'
            +'            <td> <label> Tags:  </label> ' + tagStr + '</td>'
            +'        </tr>'
            +'        <tr>'
            +'            <table class="table table-condensed">'
            +'                 <label> Properties: </label> <tbody>' + propStr
            +'                </tbody>'
            +'            </table>'
            +'        </tr>'
            +'        <tr> <label> Exceptions: </label> '
            +'            <table class="table">'
            +'                <tbody>' + exceptStr
            +'                </tbody>'
            +'            </table>'
            +'        </tr>'
            +'    </tbody>'
            +'</table>';
    }


    , update3D: function () {

        var dotCoords = [];
        var _locSet = {};
        var _locCount = 0;

        var oTable = document.getElementById('prma-log-table');
        //gets rows of table
        var rowLength = oTable.rows.length;

        var timeOff = new Date(oTable.rows.item(1).cells.item(0).innerHTML).getTime() / 1000 - 0.05;
        var _timeLimit = new Date(oTable.rows.item(rowLength - 1).cells.item(0).innerHTML).getTime() / 1000;
        var interval = (_timeLimit - timeOff) / 10;
//loops through rows, skip first one(<thread>)
        for (var i = 1; i < rowLength; i++) {

            //gets cells of current row
            var oCells = oTable.rows.item(i).cells;

            var time = (new Date(oCells.item(0).innerHTML).getTime() / 1000 - timeOff) / interval;
            var level = Prma.level2Int[oCells.item(1).innerHTML];
            var _loc = oCells.item(2).innerHTML;
            var loc = _locSet[_loc];


            if (!loc) {
                loc = _locCount;
                _locSet[_loc] = loc;
                _locCount = _locCount + 1;
            }
            dotCoords.push([time, level, loc]);
            console.log(oCells.item(0).innerHTML + "   " + oCells.item(1).innerHTML + "    " + oCells.item(2).innerHTML);
            console.log(time + "   " + level + "   " + loc)
        }

        // Give the points a 3D feel by adding a radial gradient
        Highcharts.getOptions().colors = $.map(Highcharts.getOptions().colors, function (color) {
            return {
                radialGradient: {
                    cx: 0.4,
                    cy: 0.3,
                    r: 0.5
                },
                stops: [
                    [0, color],
                    [1, Highcharts.Color(color).brighten(-0.2).get('rgb')]
                ]
            };
        });

// Set up the chart
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'log-3d-axis',
                margin: 80,
                type: 'scatter',
                options3d: {
                    enabled: true,
                    alpha: 10,
                    beta: 30,
                    depth: 250,
                    viewDistance: 5,

                    frame: {
                        bottom: {size: 1, color: 'rgba(0,0,0,0.02)'},
                        back: {size: 1, color: 'rgba(0,0,0,0.04)'},
                        side: {size: 1, color: 'rgba(0,0,0,0.06)'}
                    }
                }
            },
            title: {
                text: ''
            },
            plotOptions: {
                scatter: {
                    width: 10,
                    height: 10,
                    depth: 10
                }
            },
            yAxis: {
                min: 0,
                max: 10,
                title: "level"
            },
            xAxis: {
                min: 0,
                max: 10,
                gridLineWidth: 1,
                title: "time"
            },
            zAxis: {
                min: 0,
                max: 10,
                title: "logger"
            },
            legend: {
                enabled: false
            },
            series: [{
                name: 'Reading',
                colorByPoint: true,
                data: dotCoords
            }]
        });

// Add mouse events for rotation
        $(chart.container).bind('mousedown.hc touchstart.hc', function (e) {
            e = chart.pointer.normalize(e);

            var posX = e.pageX,
                posY = e.pageY,
                alpha = chart.options.chart.options3d.alpha,
                beta = chart.options.chart.options3d.beta,
                newAlpha,
                newBeta,
                sensitivity = 3; // lower is more sensitive

            $(document).bind({
                'mousemove.hc touchdrag.hc': function (e) {
                    // Run beta
                    newBeta = beta + (posX - e.pageX) / sensitivity;
                    newBeta = Math.min(100, Math.max(-100, newBeta));
                    chart.options.chart.options3d.beta = newBeta;

                    // Run alpha
                    newAlpha = alpha + (e.pageY - posY) / sensitivity;
                    newAlpha = Math.min(100, Math.max(-100, newAlpha));
                    chart.options.chart.options3d.alpha = newAlpha;

                    chart.redraw(false);
                },
                'mouseup touchend': function () {
                    $(document).unbind('.hc');
                }
            });
        });



    }
};