package com.caibowen.prma.store.dao;

import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 6-11-2014.
 */
public interface MarkerDAO extends Int4DAO<String> {

    boolean insertIfAbsent(long eventId, Set<String> markers);
    boolean insertAll(long eventId, Set<String> markers);
    boolean hasKey(int hash);
}
