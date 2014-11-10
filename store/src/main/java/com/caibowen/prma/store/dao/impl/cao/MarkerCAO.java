package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.prma.store.dao.MarkerDAO;
import com.caibowen.prma.store.dao.impl.MarkerDAOImpl;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BowenCai
 * @since 10-11-2014.
 */
public class MarkerCAO extends Int4LIRSCAO<String> implements MarkerDAO {

    @Inject MarkerDAO db;

    @Override
    public boolean insertIfAbsent(long eventId, Set<String> markers) {
        Long eid = eventId;
        TreeSet<String> mks = new TreeSet<>();
        synchronized (db) {
            for (String e : markers) {
                Integer k = e.hashCode();
                if (null == cache.put(k, e) && !hasKey(k))
                    mks.add(e);
            }

            if (mks.isEmpty())
                return true;

            boolean ret = db.insertAll(eventId, mks);
            if (!ret)
                for (String e2 : markers)
                    cache.remove(e2.hashCode());
            return ret;
        }
    }

    @Override
    synchronized public boolean insertAll(long eventId, Set<String> markers) {
        boolean ret = db.insertAll(eventId, markers);
        if (ret)
            for (String s : markers)
                cache.put(s.hashCode(), s);
        return false;
    }
}
