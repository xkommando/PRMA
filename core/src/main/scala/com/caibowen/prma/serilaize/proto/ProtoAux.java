package com.caibowen.prma.serilaize.proto;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.google.protobuf.ByteString;
import scala.NotImplementedError;
import scala.Option;
import scala.collection.convert.WrapAsJava$;
import scala.collection.immutable.Vector;

import java.util.List;
import java.util.Map;
import scala.collection.convert.WrapAsJava;

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

        Option<?> op = e.message();
        if (op.isDefined())
            eb.setExceptionMsg((String)op.get());

        eb.setExceptionName(e.name())
                .setId(e.id());

        op = e.stackTraces();
        if (op.isDefined()) {
            List<StackTraceElement> ss = WrapAsJava$.MODULE$.seqAsJavaList((Vector<StackTraceElement>)op.get());
            for (StackTraceElement st : ss)
                eb.addStacktraces(buildStackTrace(st));
        }
        Exception.ExceptionPO p = eb.build();
        eb.clear();
        return p;
    }

    public static Event.EventPO buildEvent(EventVO e) {
        Event.EventPO.Builder eb = eventTB.get().clear();
        eb.setCallerSt(buildStackTrace(e.callerStackTrace()))
                .setFmtMsg(e.message())
                .setId(e.id())
                .setLevel(e.level().id())
                .setLoggerName(e.loggerName())
                .setThreadName(e.threadName())
                .setTimeCreated(e.timeCreated());

        Option<?> _op = e.exceptions();
        if (_op.isDefined()) {
            List<ExceptionVO> exs = WrapAsJava$.MODULE$.seqAsJavaList((Vector<ExceptionVO>)_op.get());
            if (exs != null && exs.size() > 0)
                for (ExceptionVO ex : exs)
                    eb.addExceptions(buildExcept(ex));
        }

        _op = e.properties();
        if (_op.isDefined()) {
            Map<String, Object> m = (Map<String, Object>)_op.get();
            if (m != null && m.size() > 0)
                for (Map.Entry<String, Object> ety : m.entrySet())
                    eb.addProperties(buildProp(ety));
        }

        Event.EventPO p = eb.build();
        eb.clear();
        return p;
    }

    public static EventVO fromEventPO(Event.EventPO po) {
        throw new NotImplementedError();
    }

}