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
var logTable = $('#prma-log-table');

$(document).ready(function () {

    //var dtable = logTable.dataTable();
    var dtable = logTable.DataTable(PrmaLog.dtableOptions);
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

    console.log("fk10");
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
            var evId = row.data().id;
            var evFlag = row.data().flag;
            $.get("testdata2.txt", {id: evId, flag: evFlag})
                .done(function (resp) {
                    var data = JSON.parse(resp).data;
                    var htmlStr = PrmaLog.logDetail(data);
                    row.child(htmlStr).show();
                    tr.addClass('shown');
                });
        }
    } );

    console.log("fk11");

});

$('#tq-btn').click(function () {
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

    var q = {
        "minTime": minTime,
        "maxTime": maxTime,
        "lowLevel": llevel,
        "highLevel": hlevel,
        "exceptionOnly": exceptOnly
    };

    if (lgName)
        q["loggerName"] = lgName;
    if (threadName)
        q["threadName"] = threadName;
    if (msg) {
        q["message"] = msg;
        q["fuzzyQuery"] = msgFuzzyQ;
    }
    q = Prma.encodeQueryData(q);
    console.log(q);
    logTable.api().ajax.url("logs.json?" + q).load();
    //$.get( "http://localhost:63342/PRMA/webface/src/main/webapp", q)
    //    .done(function( data ) {
    //        alert( "Data Loaded: " + data );
    //    });
});


var PrmaLog = {
    dtableOptions: {
        "ajax": "testdata.txt?minTime=1&maxTime=2147483647&lowLevel=TRACE&highLevel=FATAL&exceptionOnly=false",
        //"ajax": "log.json?minTime=1&maxTime=2147483647&lowLevel=TRACE&highLevel=FATAL&exceptionOnly=false",
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
                var head = '<td><label class="label label-danger">' + except.name + '</label><hr/><label class="label label-info">' + except.message + '</label></td>';

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
}