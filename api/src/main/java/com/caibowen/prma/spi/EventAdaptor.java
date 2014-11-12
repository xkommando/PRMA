package com.caibowen.prma.spi;

import com.caibowen.prma.api.model.EventVO;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public interface EventAdaptor<V> {

    EventVO from(V otherEvent);
    V to(EventVO vo);
}
