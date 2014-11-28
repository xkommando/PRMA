package com.caibowen.prma.core.filter.basic;

import com.caibowen.gplume.common.collection.URIPrefixTrie;
import com.caibowen.gplume.common.collection.URISuffixTrie;
import com.caibowen.gplume.misc.Str;

/**
 * @author BowenCai
 * @since 6-11-2014.
 */
public class PartialStrFilter extends StrFilter {

    /**
     * match suffix
     */
    URISuffixTrie<Object> ignoreSuffix = new URISuffixTrie<>();
    /**
     * match prefix
     */
    URIPrefixTrie<Object> ignorePrefix = new URIPrefixTrie<>();

    @Override
    public void addMatch(String buf) {
        /**
         * skip # comment;
         */
        int idx = buf.indexOf('#');
        if (idx != -1 && Str.Utils.isBlank(buf.substring(0, idx)))
            return;

        if (buf.startsWith("*"))
            ignoreSuffix.makeBranch(buf.substring(0, buf.length() - 1), NA);
        else if (buf.endsWith("*"))
            ignorePrefix.makeBranch(buf.substring(0, buf.length() - 1), NA);
        else
            ignoreByFullMatch.put(buf, NA);
    }

    /**
     *
     * @param e
     * @return 1 if contains
     */
    @Override
    protected int doAccept(String e) {
        return ignoreByFullMatch.get(e) != null
                || null != ignorePrefix.matchPrefix(e)
                || null != ignoreSuffix.matchSuffix(e) ? 1 : -1;
    }

    @Override
    public void stop() {
        ignorePrefix.clear();
        ignoreSuffix.clear();
        super.stop();
    }
}
