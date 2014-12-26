/**
 * Created by Bowen Cai on 12/25/2014.
 */

console.log("fk7");

$(".form_datetime").datetimepicker({
    format: 'yyyy-mm-dd hh:ii:ss',
    todayHighlight: true,
    todayBtn: true,
    minuteStep: 1,
    autoclose: true
});


$("#tq-msg-fuzzy").bootstrapSwitch();
$("#tq-only-except").bootstrapSwitch();


console.log("fk6");

$(function () {

    $('#side-menu').metisMenu();

    $(window).bind("load resize", function () {
        var topOffset = 50;
        var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        var height = (this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    });
});

function fmtDate(num) {
    var date = new Date(num);
    var year = date.getFullYear();
    return year + "-" + date.getMonth() + "-" + date.getDate()
        + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
}
function encodeQueryData(data) {
    var ret = [];
    for (var d in data)
        ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
    return ret.join("&");
}
var dtable = $('#prma-log-table');
$(document).ready(function () {

    $(window).scroll(function () {
        if ($(this).scrollTop() > 80) {
            $('#back-to-top').fadeIn();
        } else {
            $('#back-to-top').fadeOut();
        }
    });
    var $root = $('html, body');
    // scroll body to 50px on click
    $('#back-to-top').click(function () {
        $('#back-to-top').tooltip('hide');
        $root.animate({
            scrollTop: 0
        }, 800);
        return false;
    });

    console.log("fk9");
    dtable.dataTable(
        {
            "ajax": "testdata.json?minTime=1&maxTime=2147483647&lowLevel=TRACE&highLevel=FATAL&exceptionOnly=false",
            "columns": [
                {
                    "data": "timeCreated",
                    "width": "10%"
                }, // time
                {
                    "data": "level",
                    "width": "3%"
                }, // level
                {
                    "data": "loggerName",
                    "width": "15%"
                }, // logger
                {
                    "data": "message",
                    "width": "40%"
                }, // message
                {
                    "data": "threadName",
                    "width": "8%"
                } // message
            ],
            "columnDefs": [
                {
                    "render": function (data, type, row) {
                        return fmtDate(row.timeCreated);
                    },
                    "targets": 0
                }
            ]
        }
    );
    console.log("fk10");

});
var tst = 1;
$('#tq-btn').click(function(){
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

    var q = {"minTime": minTime,
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
    q = encodeQueryData(q);
    console.log(q);
    //dtable.ajax.url("log.json?" + q).load();
    //$.get( "http://localhost:63342/PRMA/webface/src/main/webapp", q)
    //    .done(function( data ) {
    //        alert( "Data Loaded: " + data );
    //    });
});

console.log("fk11");