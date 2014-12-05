//package com.caibowen.prma.store.rdb.dao.impl.cao;
//
//import com.caibowen.prma.spi.Int8CacheProvider;
//import com.caibowen.prma.store.rdb.dao.MarkerDAO;
//
//import javax.inject.Inject;
//import java.util.Set;
//import java.util.TreeSet;
//
///**
// * @author BowenCai
// * @since 10-11-2014.
// */
//public class MarkerCAO extends Int4LIRSCAO<String> implements MarkerDAO {
//
//    @Inject MarkerDAO db;
//
//    @Inject
//    Int8CacheProvider eventCache;
//
//    @Override
//    public Set<String> getByEvent(long eventId) {
//        Set<String> ret = (Set<String>)eventCache.get(eventId);
//        if (ret == null) {
//            ret = db.getByEvent(eventId);
//            if (ret != null)
//                eventCache.put(eventId, ret);
//        }
//        return ret;
//    }
//
//    @Override
//    public boolean insertIfAbsent(long eventId, Set<String> markers) {
//        Long eid = eventId;
//        TreeSet<String> mks = new TreeSet<>();
//        synchronized (db) {
//            for (String e : markers) {
//                Integer k = e.hashCode();
//                if (!cache.contains(k) && !hasKey(k))
//                    mks.add(e);
//            }
//
//            if (mks.isEmpty())
//                return true;
//
//            boolean ret = db.insertAll(eventId, mks);
//            return ret;
//        }
//    }
//
//    @Override
//    synchronized public boolean insertAll(long eventId, Set<String> markers) {
//        boolean ret = db.insertAll(eventId, markers);
//        if (ret) {
//            eventCache.put(eventId, markers);
//            for (String s : markers)
//                cache.put(s.hashCode(), s);
//        }
//        return true;
//    }
//}
