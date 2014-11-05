package com.caibowen.prma.spec;

import com.caibowen.prma.api.model.EventVO;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public interface EventTranslator<V> {

    EventVO to(V otherEvent);
    V from(EventVO vo);
}
