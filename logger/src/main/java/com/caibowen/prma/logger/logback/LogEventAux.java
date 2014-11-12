package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.gplume.common.collection.ImmutableArraySet;
import com.caibowen.prma.api.LogLevel;
import com.caibowen.prma.api.model.ExceptionVO;
import org.slf4j.Marker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public final class LogEventAux {

    private LogEventAux(){}

    public static LogLevel level(Level le) {
        int val = le.levelInt / Level.TRACE_INT;
        return LogLevel.values()[4 - val / 2];
    }


    public static
    @Nullable Map<String, String> extractProperties(ILoggingEvent event) {

        Map<String, String> _mdc = event.getMDCPropertyMap();
        final int _mdcSz = _mdc.size();
        Map<String, String> _ctx = event.getLoggerContextVO()
                .getPropertyMap();
        final int _ctxSz = _ctx.size();

        if (_mdcSz > 0 &&  _ctxSz > 0) {
            int sz = _mdcSz + _ctxSz;
            Map<String, String> ret = new HashMap<>(sz * 4 / 3 + 1);
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

    public static
    byte level(ILoggingEvent event) {
        return  (byte)(event.getLevel().levelInt / Level.TRACE_INT);
    }



}
