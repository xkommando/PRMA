package com.caibowen.prma.core.filter;

import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.InputStreamCallback;
import com.caibowen.gplume.context.InputStreamProviderProxy;
import com.caibowen.gplume.context.InputStreamSupport;
import com.caibowen.gplume.misc.Str;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.StreamSupport;

/**
 * Filter by prefix, suffix or entire string
 *
 *
 * @author BowenCai
 * @since 30-10-2014.
 */
public class StrFilter extends AbstractFilter<String> {

    @Inject private String configPath;
    public String getConfigPath() {
        return configPath;
    }
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    @Override
    protected int doAccept(String e) {
        return null == ignoreByFullMatch.get(e) ? 1 : -1;
    }

    HashMap<String, Object> ignoreByFullMatch = new HashMap<>(128);

    static Object NA = new Object();

    protected void handle(String buf) {

        /**
         * skip # comment;
         */
        int idx = buf.indexOf('#');
        if (idx != -1 && Str.Utils.isBlank(buf.substring(0, idx)))
            return;

        ignoreByFullMatch.put(buf, NA);
    }

    public void start() {
        InputStreamSupport streamSupport = InputStreamSupport.DEFAULT_SUPPORT;
        streamSupport.withPath(configPath, new InputStreamCallback() {
            @Override
            public void doInStream(InputStream stream) throws Exception {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String buf;
                while (null != (buf = reader.readLine())) {
                    if (Str.Utils.notBlank(buf))
                        handle(buf);
                }

                reader.close();
            }
        });
        started = true;
    }

    @Override
    public void stop() {
        ignoreByFullMatch.clear();
        started = false;
    }



}
