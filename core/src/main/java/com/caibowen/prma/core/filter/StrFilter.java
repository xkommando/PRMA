package com.caibowen.prma.core.filter;

import com.caibowen.gplume.common.URIPrefixTrie;
import com.caibowen.gplume.common.URISuffixTrie;
import com.caibowen.gplume.misc.Str;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Filter by prefix, suffix or entire string
 *
 *
 * @author BowenCai
 * @since 30-10-2014.
 */
public class StrFilter extends AbstractFilter<String> {

    @Override
    protected int doAccept(String e) {
        return null == ignoreByFullMatch.get(e)
                && null == ignorePrefix.matchPrefix(e)
                && null == ignoreSuffix.matchPrefix(e) ? 1 : -1;
    }

    static HashMap<String, Object> ignoreByFullMatch = new HashMap<>(128);
    /**
     * match suffix
     */
    static URISuffixTrie<Object> ignoreSuffix = new URISuffixTrie<>();
    /**
     * match prefix
     */
    static URIPrefixTrie<Object> ignorePrefix = new URIPrefixTrie<>();

    Object NA = new Object();

    protected void handle(String buf) {

        /**
         * skip # comment;
         */
        int idx = buf.indexOf('#');
        if (idx != -1 && Str.Utils.isBlank(buf.substring(0, idx)))
            return;


        if (buf.startsWith("*"))
            ignoreSuffix.branch(buf.substring(0, buf.length() - 1), NA);
        else if (buf.endsWith("*"))
            ignorePrefix.branch(buf.substring(0, buf.length() - 1), NA);
        else
            ignoreByFullMatch.put(buf, NA);
    }

    public void start() {

        InputStream ins = Filter.class.getClassLoader().getResourceAsStream("ignored_exceptions.txt");
        BufferedReader reader = null;
        try {
            if (ins == null || ins.available() <= 0)
                return;
            reader = new BufferedReader(new InputStreamReader(ins));
            String buf;
            while (null != (buf = reader.readLine())) {
                if (Str.Utils.notBlank(buf)) {
                    handle(buf);
                }
            }
        } catch (Throwable e ) {
            // ????
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (ins != null)
                    ins.close();
            } catch (Throwable e) {
                // LOGGGG
                e.printStackTrace();
            }
        }

        started = true;
    }

    @Override
    public void stop() {
        NA = null;
        ignoreByFullMatch.clear();
        ignorePrefix.clear();
        started = false;
    }



}
