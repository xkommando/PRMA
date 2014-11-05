package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public final class LogEventAux {

    private LogEventAux(){}


    public static
    @Nullable Map<String, String> extractProperties(ILoggingEvent event) {

        Map<String, String> _mdc = event.getMDCPropertyMap();
        final int _mdcSz = _mdc.size();
        Map<String, String> _ctx = event.getLoggerContextVO()
                .getPropertyMap();
        final int _ctxSz = _ctx.size();

        if (_mdcSz > 0 &&  _ctxSz > 0) {
            int sz = _mdcSz + _ctxSz;
            Map<String, String> ret = new HashMap(sz * 4 / 3 + 1);
            ret.putAll(_mdc);
            ret.putAll(_ctx);
            return ret;
        } else if (_mdcSz > 0)
            return _mdc;
        else if (_ctxSz > 0)
            return _ctx;
        else
            return null;
    }


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


//    trace 8
//    DEBUG level is converted to 7,
// INFO is converted to 6,
// WARN is converted to 4
// and ERROR is converted to 3.

    public static
    byte level(ILoggingEvent event) {
        return  (byte)(event.getLevel().levelInt / Level.TRACE_INT);
    }



    public static final short PROPERTIES_EXIST = 0x01;
    public static final short EXCEPTION_EXISTS = 0x02;

    public static byte flag(ILoggingEvent event) {
        byte mask = 0;
//
//        int mdcPropSize = 0;
//        if (event.getMDCPropertyMap() != null) {
//            mdcPropSize = event.getMDCPropertyMap().keySet().size();
//        }
//        int contextPropSize = 0;
//        if (event.getLoggerContextVO().getPropertyMap() != null) {
//            contextPropSize = event.getLoggerContextVO().getPropertyMap().size();
//        }
//
//        if (mdcPropSize > 0 || contextPropSize > 0) {
//            mask = PROPERTIES_EXIST;
//        }
//        if (event.getThrowableProxy() != null) {
//            mask |= EXCEPTION_EXISTS;
//        }
        return mask;
    }

}
