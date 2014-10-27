package com.caibowen.prma.jdbc;

import com.caibowen.gplume.misc.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public final class SyncCenter {

    private static final ThreadLocal<Boolean> activeRecord = new ThreadLocal<>();
    private static final ThreadLocal<Map> resources = new ThreadLocal<>();

    public static final void setActive(boolean boo) {
        activeRecord.set(boo ? Boolean.TRUE : Boolean.FALSE);
    }

    public static final boolean isActive() {
        return activeRecord.get() == Boolean.TRUE;
    }

    public static final @Nullable Object
    get(@Nonnull Object key) {
        Map m = resources.get();
        if (m == null)
            return null;
        return m.get(key);
    }

    public static Object add(@Nonnull Object key, @Nonnull Object resource) {
        Map map = resources.get();
        if (map == null) {
            map = new HashMap(8);
            map.put(key, resource);
            resources.set(map);
            return null;
        }
        return map.put(key, resource);
    }

    public static @Nullable Object
    remove(@Nonnull Object key) {
        Map m = resources.get();
        if (m == null)
            return null;
        return m.remove(key);
    }


}
