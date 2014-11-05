package com.caibowen.prma.api.proto;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.google.protobuf.ByteString;

import java.util.List;
import java.util.Map;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public final class ProtoAux {

    private ProtoAux(){}

    public static final ThreadLocal<Event.EventPO.Builder> eventTB = new ThreadLocal<Event.EventPO.Builder>(){
        @Override
        protected Event.EventPO.Builder initialValue() {
            return Event.EventPO.newBuilder();
        }
    };

    public static final ThreadLocal<StackTrace.StackTracePO.Builder> stackTB = new ThreadLocal<StackTrace.StackTracePO.Builder>(){
        @Override
        protected StackTrace.StackTracePO.Builder initialValue() {
            return StackTrace.StackTracePO.newBuilder();
        }
    };

    public static final ThreadLocal<Exception.ExceptionPO.Builder> exceptTB = new ThreadLocal<Exception.ExceptionPO.Builder>(){
        @Override
        protected Exception.ExceptionPO.Builder initialValue() {
            return Exception.ExceptionPO.newBuilder();
        }
    };
    public static final ThreadLocal<Event.EventPO.Property.Builder> propTB = new ThreadLocal<Event.EventPO.Property.Builder>(){
        @Override
        protected Event.EventPO.Property.Builder initialValue() {
            return Event.EventPO.Property.newBuilder();
        }
    };

    public static StackTrace.StackTracePO buildStackTrace(StackTraceElement ste) {

        StackTrace.StackTracePO.Builder sb = stackTB.get().clear();
        StackTrace.StackTracePO v =
                sb.setClassName(ste.getClassName())
                .setFileName(ste.getFileName())
                .setFunctionName(ste.getMethodName())
                .setLineNumber(ste.getLineNumber()).build();
        sb.clear();
        return v;
    }

    public static Event.EventPO.Property buildProp(Map.Entry e) {
        Event.EventPO.Property.Builder pb = propTB.get().clear();
        ByteString bs = ByteString.copyFrom(((String) (e.getValue())).getBytes());
        Event.EventPO.Property p = pb.setMapKey((String)e.getKey())
                .setMapValue(bs).build();
        pb.clear();
        return p;
    }

    public static Exception.ExceptionPO buildExcept(ExceptionVO e) {
        Exception.ExceptionPO.Builder eb = exceptTB.get().clear();

        eb.setExceptionMsg(e.exceptionMessage)
                .setExceptionName(e.exceptionName)
                .setId(e.id);

        for (StackTraceElement st : e.stackTraces)
            eb.addStacktraces(buildStackTrace(st));

        Exception.ExceptionPO p = eb.build();
        eb.clear();
        return p;
    }

    public static Event.EventPO buildEvent(EventVO e) {
        Event.EventPO.Builder eb = eventTB.get().clear();
        eb.setCallerSt(buildStackTrace(e.callerStackTrace))
                .setFmtMsg(e.message)
                .setId(e.id)
                .setLevel(e.level.levelInt)
                .setLoggerName(e.loggerName)
                .setThreadName(e.threadName)
                .setTimeCreated(e.timeCreated);

        List<ExceptionVO> exs = e.exceptions;
        if (exs != null && exs.size() > 0)
            for (ExceptionVO ex : exs)
                eb.addExceptions(buildExcept(ex));

        Map<String, Object> m = e.properties;
        if (m != null && m.size() > 0)
            for (Map.Entry<String, Object> ety : m.entrySet())
                eb.addProperties(buildProp(ety));

        Event.EventPO p = eb.build();
        eb.clear();
        return p;
    }

    public static EventVO fromEventPO(Event.EventPO po) {
        EventVO v = new EventVO();


        return v;
    }

}
