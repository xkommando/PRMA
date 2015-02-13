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

    //$.get("testdata-inspect.txt", q)
    $.get("/inspect/q", q)
        .done(function (resp) {
            var obj = JSON.parse(resp);
            if (obj.code != 200) {
                alert(obj.message);
                return;
            }

            var data = obj.data;

            //$('#table-entropy5').highcharts({///})
        });


});

//http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/polar-spider/

var PrmaInsp = {

};