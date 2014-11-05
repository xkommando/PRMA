package com.caibowen.prma.store;

import com.caibowen.prma.api.model.EventVO;

import java.util.List;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public interface EventPersist {

    void persist(EventVO event);

    void batchPersist(List<EventVO> ls);
}
