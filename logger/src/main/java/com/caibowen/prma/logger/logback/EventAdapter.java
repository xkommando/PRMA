package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.gplume.misc.Hashing;
import com.caibowen.gplume.misc.Str;
import com.caibowen.prma.api.LogLevel;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.spec.EventTranslator;
import com.caibowen.prma.store.ExceptionDO;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class EventAdapter implements EventTranslator<ILoggingEvent> {

    public static final int[] EMPTY_INTS = {};

    @Override
    public EventVO to(ILoggingEvent event) {

        Map prop = LogEventAux.extractProperties(event);

        IThrowableProxy px = event.getThrowableProxy();
        final ArrayList<ExceptionVO> vols = (px != null ? new ArrayList<ExceptionVO>(16) : null);
        while (px != null) {
            StackTraceElementProxy[] stps = px.getStackTraceElementProxyArray();
            StackTraceElement[] sts = new StackTraceElement[stps.length];
            for (int i = 0; i < stps.length; i++) {
                sts[i] = stps[i].getStackTraceElement();
            }
            vols.add(new ExceptionVO(px.getClassName(), px.getMessage(), sts));

            px = px.getCause();
        }

        return new EventVO(event.getTimeStamp(), level(event.getLevel()),
                event.getLoggerName(), event.getThreadName(),
                callerST(event),
                event.getFormattedMessage(),
                prop, vols);
    }

    public static LogLevel level(Level le) {
        int val = le.levelInt / Level.TRACE_INT;
        return LogLevel.values()[4 - val / 2];
    }


    public static final StackTraceElement NA_ST = new  StackTraceElement("?", "?", "?", -1);
    public static
    @Nonnull
    StackTraceElement callerST(ILoggingEvent event) {

        StackTraceElement[] _callerSTs = event.getCallerData();
        StackTraceElement callerST;
        if (_callerSTs != null && _callerSTs.length > 0)
            callerST = _callerSTs[0];
        else
            callerST = NA_ST;

        return callerST;
    }

    @Override
    public ILoggingEvent from(EventVO vo) {
        throw new NoSuchMethodError("not implemented yet");
    }
}
