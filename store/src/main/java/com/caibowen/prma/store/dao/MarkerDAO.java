package com.caibowen.prma.store.dao;

import com.caibowen.prma.api.model.ExceptionVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 6-11-2014.
 */
public interface MarkerDAO extends Int4DAO<String> {

    boolean insertIfAbsent(long eventId, Set<String> markers);
    boolean insertAll(long eventId, Set<String> markers);

    Set<String> getByEvent(long eventId);
}
