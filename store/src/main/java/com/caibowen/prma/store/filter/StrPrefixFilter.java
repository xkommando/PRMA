package com.caibowen.prma.store.filter;

import com.caibowen.gplume.common.URITrie;
import com.caibowen.gplume.misc.Str;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Filter by String or String prefix
 *
 * @author BowenCai
 * @since 30-10-2014.
 */
public class StrPrefixFilter extends AbstractFilter<String> {

    @Override
    protected boolean doAccept(String e) {
        return null == ignoreByFullMatch.get(e)
                && null == ignorePrefix.matchPrefix(e);
    }

    static HashMap<String, Object> ignoreByFullMatch = new HashMap<>(128);
    /**
     * match prefix
     */
    static URITrie<Object> ignorePrefix = new URITrie<>();

    Object NA = new Object();

    protected void handle(String buf) {

        /**
         * skip # comment;
         */
        int idx = buf.indexOf('#');
        if (idx != -1 && Str.Utils.isBlank(buf.substring(0, idx)))
            return;


        if (buf.endsWith("*"))
            ignorePrefix.branch(buf.substring(0, buf.length() - 1), NA);
        else
            ignoreByFullMatch.put(buf, NA);
    }

    public void init() {

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
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (ins != null)
                    ins.close();
            } catch (Throwable e) {
                // LOGGGG
            }
        }


    }

}
