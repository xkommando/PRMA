/**
 * Created by Bowen Cai on 12/25/2014.
 */



$(function () {
    //$('#side-menu').metisMenu();
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

// cached data
var $root = $('html, body');

$(document).ready(function () {

    $(window).scroll(function () {
        if ($(this).scrollTop() > 80) {
            $('#back-to-top').fadeIn();
        } else {
            $('#back-to-top').fadeOut();
        }
    });
    // scroll body to 50px on click
    $('#back-to-top').click(function () {
        $('#back-to-top').tooltip('hide');
        $root.animate({
            scrollTop: 0
        }, 800);
        return false;
    });

});

var Prma = {

    fmtDate: function (num) {
        var date = new Date(num);
        var year = date.getFullYear();
        return year + "-" + date.getMonth() + "-" + date.getDate()
            + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    }

    , encodeQueryData: function (map) {
        var ret = [];
        for (var d in map)
            ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(map[d]));
        return ret.join("&");
    }
// MyClass => mash(MyClass.java:9)
//    (file.java:9) MyClass->mash
    , fmtStackTrace: function (stackTrace) {
        var line = stackTrace["line"];
        if (line && line != -1)
            return "(" + stackTrace.file + ":" + stackTrace["line"] + ")" + stackTrace.className + " -> " + stackTrace["function"];
        else
            return "Undefined";
    }

    , fmtLocation: function(stackTrace, loggerName) {
        var line = stackTrace["line"];
        if (line && line != -1)
            return "(" + stackTrace.file + ":" + stackTrace["line"] + ")" + stackTrace.className + " -> " + stackTrace["function"];
        else
            return loggerName;
    }

    , int2Level: {
        0: "ALL",
        1: "TRACE",
        2: "DEBUG",
        4: "INFO",
        6: "WARN",
        8: "ERROR",
        16: "FATAL",
        32: "OFF"
    }

    , level2Int: {
        "ALL" : 0,
        "TRACE": 1,
        "DEBUG": 2,
        "INFO": 4,
        "WARN": 6,
        "ERROR": 8,
        "FATAL": 16,
        "OFF": 32
    }

}