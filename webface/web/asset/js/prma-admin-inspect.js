/**
 * Created by Bowen Cai on 1/26/2015.
 */

$(".form_datetime").datetimepicker({
    format: 'yyyy-mm-dd hh:ii:ss',
    todayHighlight: true,
    todayBtn: true,
    minuteStep: 1,
    autoclose: true
});
$("#btn-load").click(function() {

    var ins2Except = $('#ins2-except').val();
    var ins3SQL =  $('#ins3-sql').val();
    var q = {};

    if (ins2Except) {
        q = {
            "type": 2,
            "ins2Except": ins2Except
        };
    }

    else if (ins3SQL) {
        q = {
            "type": 3,
            "ins3SQL": ins3SQL
        };

    } else {

        var minTime = $('#ins1-minTime').val();
        var maxTime = $('#ins1-maxTime').val();
        var lowLevel = $('#ins1-lowLevel').val();
        var highLevel = $('#ins1-highLevel').val();

        maxTime = maxTime ? new Date(maxTime).getTime() : new Date().getTime();
        minTime = minTime ? new Date(minTime).getTime() : maxTime - 86400000; // last day
        lowLevel = Prma.level2Int[lowLevel];
        highLevel = Prma.level2Int[highLevel];

        if (maxTime <= minTime) {
            $('#ins1-minTime').val("");
            $('#ins1-maxTime').val("");
            alert("End time is earlier than start time");
            return;
        }
        q = {
            "type" : 1,
            "minTime" : minTime,
            "maxTime" : maxTime,
            "lowLevel": lowLevel,
            "highLevel" : highLevel
        };
    }

    $.get("testdata-inspect.txt", q)
    //$.get("/inspect/q", q)
        .done(function (resp) {
            var obj = JSON.parse(resp);
            if (obj.code != 200) {
                alert(obj.message);
                return;
            }

            var ls = obj.data._1;
            var e5 = obj.data._2;

            $('#log-entropy5').highcharts(PrmaInsp.spiderChart(e5));

            var dtable = $('#prma-ins-log-table').DataTable(PrmaInsp.dtableOptions(ls));

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

            $('#prma-ins-log-table tbody').on('click', 'td.details-control', function () {
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
                    //$.get("log/detail.json", qp)\
                    $.get("testdata-detail.txt", qp)
                        .done(function (resp) {
                            var obj = JSON.parse(resp); // -------------
                            if (obj.code != 200) {
                                alert(obj.message);
                                return;
                            }
                            var data = obj.data;
                            var htmlStr = PrmaInsp.logDetail(data);
                            row.child(htmlStr).show();
                            tr.addClass('shown');
                        });
                }
            } );

        });


});

//http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/polar-spider/

var PrmaInsp = {

    spiderChart: function (e5) {
        return {
            chart: {
                polar: true,
                type: 'line'
            },
            title: {
                text: 'Principal Factors',
                x: -80
            },
            pane: {
                size: '100%'
            },
            xAxis: {
                categories: ['IP', 'Thread', 'Logger', 'Caller', 'Time',
                    'Information Technology', 'Administration'],
                tickmarkPlacement: 'on',
                lineWidth: 0
            },
            yAxis: {
                gridLineInterpolation: 'polygon',
                lineWidth: 0,
                min: 0
            },
            tooltip: {
                shared: true,
                pointFormat: '<span style="color:{series.color}">{series.name}: <b>{point.y}</b><br/>'
            },
            legend: {
                align: 'right',
                verticalAlign: 'top',
                y: 70,
                layout: 'vertical'
            },
            series: [{
                name: 'Entropy',
                data: e5,
                pointPlacement: 'on'
            }]
        }
    }

    , dtableOptions: function (logLs) {
        return {
            "data": logLs,
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
                    , "width": "2%"
                    , "className": "dt-level"
                }, // level
                {
                    "data": "callerStackTrace"
                    , "width": "15%"
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


};