//package com.caibowen.prma.store.rdb.dao;
//
//import com.caibowen.gplume.common.Pair;
//
//import java.util.Map;
//
///**
// * @author BowenCai
// * @since 22-10-2014.
// */
//public interface PropertyDAO extends KVStore<Pair<String, String>> {
//
//    boolean insertIfAbsent(long eventId, Map<String, String> prop);
//    boolean insertAll(long eventId, Map<String, String> prop);
//    boolean hasKey(int hash);
//
//    Map<String, Object> getByEvent(long eventId);
//}
