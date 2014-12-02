package com.caibowen.prma.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.prma.api.LogLevel;
import scala.Enumeration;
import scala.collection.JavaConversions;
import scala.collection.immutable.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
* @author BowenCai
* @since 24-10-2014.
*/
public final class _LogEventAux {

//    private LogEventAux(){}
//
//    public static Enumeration.Value level(Level le) {
//        int val = le.levelInt / Level.TRACE_INT;
//        return LogLevel.from(val);
//    }


//    public static
//    @Nullable
//    Map<String, String> extractProperties(ILoggingEvent event) {
//
//        java.util.Map<String, String> _mdc = event.getMDCPropertyMap();
//        final int _mdcSz = _mdc.size();
//        java.util.Map<String, String> _ctx = event.getLoggerContextVO()
//                .getPropertyMap();
//        final int _ctxSz = _ctx.size();
//
//        if (_mdcSz > 0 &&  _ctxSz > 0) {
//            int sz = _mdcSz + _ctxSz;
//            HashMap<String, String> ret = new HashMap<>(sz * 4 / 3 + 1);
//            ret.putAll(_mdc);
//            ret.putAll(_ctx);
//            return JavaConversions.mapAsScalaMap(ret);
//        } else if (_mdcSz > 0)
//            return JavaConversions.mapAsScalaMap(_mdc);
//        else if (_ctxSz > 0)
//            return JavaConversions.mapAsScalaMap(_ctx);
//        else
//            return null;
//    }


    public static final StackTraceElement NA_ST = new  StackTraceElement("?", "?", "?", -1);

    public static
    @Nonnull StackTraceElement callerST(ILoggingEvent event) {

        StackTraceElement[] _callerSTs = event.getCallerData();
        StackTraceElement callerST;
        if (_callerSTs != null && _callerSTs.length > 0)
            callerST = _callerSTs[0];
        else
            callerST = NA_ST;

        return callerST;
    }

    public static
    byte level(ILoggingEvent event) {
        return  (byte)(event.getLevel().levelInt / Level.TRACE_INT);
    }



}
