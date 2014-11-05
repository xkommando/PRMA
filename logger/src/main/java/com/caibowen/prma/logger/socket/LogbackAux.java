package com.caibowen.prma.logger.socket;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.api.proto.*;
import com.caibowen.prma.api.proto.Exception;
import com.caibowen.prma.logger.logback.EventAdapter;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class LogbackAux {

    public static final EventAdapter ADAPTER = new EventAdapter();

    public static Event.EventPO
    build(ILoggingEvent event) {
        EventVO vo = ADAPTER.to(event);
        return ProtoAux.buildEvent(vo);
    }


}
