package com.caibowen.prma.store.dao;

import com.caibowen.prma.store.EventDO;


/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface EventDAO {

    /**
     * insertAll to DB, return valid id
     * @param po
     * @return
     */
    long insert(EventDO po);
}
