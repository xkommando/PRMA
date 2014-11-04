package com.caibowen.prma.store.dao;

import java.util.Map;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface PropertyDAO {//extends Int4DAO<Pair<String, Object>> {

    boolean insertIfAbsent(long eventId, Map<String, String> prop);
    boolean insertAll(long eventId, Map<String, String> prop);
    boolean hasKey(int hash);
}
