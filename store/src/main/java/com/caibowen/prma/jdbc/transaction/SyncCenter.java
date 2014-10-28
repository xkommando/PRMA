package com.caibowen.prma.jdbc.transaction;

import com.caibowen.gplume.misc.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public final class SyncCenter {

    private static final ThreadLocal<Boolean> activeRecord = new ThreadLocal<>();
    private static final ThreadLocal<Map> resources = new ThreadLocal<>();

    public static final void setSync(boolean boo) {
        activeRecord.set(boo ? Boolean.TRUE : Boolean.FALSE);
    }

    public static final boolean isSyncActive() {
        return activeRecord.get() == Boolean.TRUE;
    }

    @Nullable
    public static <T>  T get(@Nonnull Object key) {
        Map m = resources.get();
        if (m == null)
            return null;
        return (T)m.get(key);
    }

    public static Map<Object, Object> getResourceMap() {
        Map<Object, Object> map = resources.get();
        return (map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap());
    }

    @Nullable
    public static <T> T add(@Nonnull Object key, @Nonnull Object resource) {
        Map map = resources.get();
        if (map == null) {
            map = new HashMap(8);
            map.put(key, resource);
            resources.set(map);
            return null;
        }
        return (T)map.put(key, resource);
    }
    @Nullable
    public static  <T> T
    remove(@Nonnull Object key) {
        Map m = resources.get();
        if (m == null)
            return null;
        return (T)m.remove(key);
    }


}
