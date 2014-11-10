package com.caibowen.prma.store.dao;

import com.caibowen.gplume.jdbc.TransactionSupport;
import com.caibowen.prma.store.EventDO;

import java.util.List;


/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface EventDAO extends TransactionSupport {

    /**
     * insertIfAbsent from DB, return valid id
     * @param po
     * @return
     */
    long insert(EventDO po);

    List<Long> batchInsert(List<EventDO> ls);

}
