package com.caibowen.prma.core;

import com.caibowen.gplume.context.InputStreamCallback;
import com.caibowen.gplume.context.InputStreamProvider;
import com.caibowen.gplume.context.InputStreamProviderProxy;
import com.caibowen.gplume.context.InputStreamSupport;
import com.caibowen.gplume.context.bean.ConfigCenter;
import com.caibowen.gplume.misc.Str;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BowenCai
 * @since 14-11-2014.
 */
public class StringLoader implements LifeCycle {

    /**
     * if not set.
     * default from InputStreamProviderProxy will be used
     */
    @Inject InputStreamProvider streamProvider;
    @Inject
    List<String> paths;
    private ConcurrentHashMap<String, String> map;

    @Override
    public void start() {
        if (paths == null || paths.size() == 0)
            throw new IllegalArgumentException("paths not set!");

        for (int i = 0; i < paths.size(); i++) {
            if (Str.Utils.isBlank(paths.get(i)))
                throw new IllegalArgumentException("empty path at index " + i);
        }

        if (streamProvider == null)
            streamProvider = InputStreamProviderProxy.DEFAULT_PROXY;

        map = new ConcurrentHashMap<>(256);
        final Properties _p = new Properties();
        for (final String _path : paths) {
            new InputStreamSupport(streamProvider).withPath(_path, new InputStreamCallback() {
                @Override
                public void doInStream(InputStream stream) throws Exception {
                    if (_path.endsWith(".xml"))
                        _p.loadFromXML(stream);
                    else
                        _p.load(stream);
                }
            });
            for (Map.Entry<?, ?> e : _p.entrySet()) {
                Object nk = e.getKey();
                Object nv = e.getValue();
                if (nk instanceof String && nv instanceof String) {
                    map.put((String) nk, (String) nv);
                }
            }
            _p.clear();
        }

        started = true;
        paths = null;
        streamProvider = null;
    }


    @Nonnull
    public Set<String> immutableKeys() {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Nullable
    public String get(@Nonnull String key) {
        return map.get(key);
    }

    @Nullable
    public String put(@Nonnull String key, @Nonnull String val) {
        return map.put(key, val);
    }

    @Nullable
    public String remove(@Nonnull String key) {
        return map.remove(key);
    }

    @Override
    public void stop() {
        started = false;
        map.clear();
    }


    public InputStreamProvider getStreamProvider() {
        return streamProvider;
    }

    public void setStreamProvider(InputStreamProvider streamProvider) {
        this.streamProvider = streamProvider;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    private boolean started = false;

    @Override
    public boolean isStarted() {
        return started;
    }
}
