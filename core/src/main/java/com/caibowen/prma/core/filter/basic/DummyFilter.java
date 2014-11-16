package com.caibowen.prma.core.filter.basic;

/**
 * @author BowenCai
 * @since 13-11-2014.
 */
public class DummyFilter<V> implements Filter<V> {


    @Override
    public int accept(V v) {
        return -1;
    }

    @Override
    public Filter getNext() {
        return null;
    }

    @Override
    public void setNext(Filter ef) {
    }

    private boolean started;
    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
