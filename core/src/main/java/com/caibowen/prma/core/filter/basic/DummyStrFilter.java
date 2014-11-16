package com.caibowen.prma.core.filter.basic;

import com.caibowen.gplume.misc.Str;

/**
 * @author BowenCai
 * @since 13-11-2014.
 */
public class DummyStrFilter extends StrFilter {

    @Override
    public void start() {
        started = true;
        ignoreByFullMatch = null;
        setConfigPath(Str.EMPTY);
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public int accept(String s) {
        return -1;
    }

    @Override
    public Filter getNext() {
        return null;
    }
}
