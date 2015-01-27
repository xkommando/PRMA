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
    var minTime = $('#tq-minTime').val();
    var maxTime = $('#tq-maxTime').val();
    var lowLevel = $('#tq-lowLevel').val();
    var highLevel = $('#tq-highLevel').val();

    maxTime = maxTime ? new Date(maxTime).getTime() : new Date().getTime();
    minTime = minTime ? new Date(minTime).getTime() : maxTime - 86400000; // last day
    lowLevel = Prma.level2Int[lowLevel];
    highLevel = Prma.level2Int[highLevel];

    if (maxTime <= minTime) {
        $('#tq-minTime').val("");
        $('#tq-maxTime').val("");
        alert("End time is earlier than start time");
        return;
    }

    //$('#container').highcharts({///})

});

//http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/polar-spider/

var PrmaInsp = {

};