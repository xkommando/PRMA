package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.gplume.common.collection.ImmutableArraySet;
import com.caibowen.gplume.misc.Bytes;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.spi.EventAdaptor;
import org.slf4j.Marker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class EventAdapter implements EventAdaptor<ILoggingEvent> {

    public static final int[] EMPTY_INTS = {};

    public static final long getFlag(@Nullable Map prop, @Nullable Set<String> markers, List<ExceptionVO> exceptions) {
        short sz11 = prop != null ? (short)prop.size() : 0;
        short sz12 = markers != null ? (short)markers.size() : 0;

        int sz1 = Bytes.ints.add(sz11, sz12);
        int sz2 = prop != null ? prop.size() : 0;

        return Bytes.longs.add(sz1, sz2);
    }

    @Override
    public EventVO from(ILoggingEvent event) {

        StackTraceElement caller = LogEventAux.callerST(event);
        // TODO caller filter

        Map prop = LogEventAux.extractProperties(event);
        Set<String> makers = extractMarkers(event);
        List<ExceptionVO> exs = extractExceptionVOs(event);
        Long flag = getFlag(prop, makers, exs);

        return new EventVO(event.getTimeStamp(),
                LogEventAux.level(event.getLevel()),
                flag,
                event.getLoggerName(),
                event.getThreadName(),
                caller,
                event.getFormattedMessage(), null,
                prop,
                extractExceptionVOs(event),
                makers);
    }



    @Nullable
    public List<ExceptionVO> extractExceptionVOs(@Nonnull ILoggingEvent event) {
        IThrowableProxy px = event.getThrowableProxy();
        if (px == null)
            return null;

        IThrowableProxy _cause = px.getCause();
        if (_cause == null)
            return Arrays.asList(toExceptVO(px, 0));

        ArrayList<ExceptionVO> vols = new ArrayList<ExceptionVO>(16);
        vols.add(toExceptVO(px, 0)); // save first one.
        int commenSt = _cause.getCommonFrames(); // note use commonSt from cause
        while (_cause != null) {
            // TODO exception filter

            vols.add(toExceptVO(px, commenSt));
            _cause = _cause.getCause();
        }
        return vols;
    }


    @Nonnull
    public ExceptionVO toExceptVO(IThrowableProxy px, int start) {
        StackTraceElementProxy[] stps = px.getStackTraceElementProxyArray();
        ArrayList<StackTraceElement> sts = new ArrayList<>(stps.length - start);
        for (int i = 0; i < stps.length - start; i++) {
            // TODO stack trace filter
            sts.add(stps[i].getStackTraceElement());
        }
        StackTraceElement[] arr = new StackTraceElement[sts.size()];
        return new ExceptionVO(px.getClassName(), px.getMessage(), sts.toArray(arr));
    }



    @Nullable
    public Set<String> extractMarkers(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker == null)
            return null;
        Iterator<Marker> iter = marker.iterator();
        if (!iter.hasNext())
            // one marker
            return new ImmutableArraySet<>(new Object[]{marker.getName()});

        Set<String> ret = new HashSet<>(8);
        while (iter.hasNext()) {
            // TODO marker filter
            ret.add(iter.next().getName());
        }
        // last one marker
        ret.add(marker.getName());
        return ret;
    }


    @Override
    public ILoggingEvent to(EventVO vo) {
        throw new NoSuchMethodError("not implemented yet");
    }
}
