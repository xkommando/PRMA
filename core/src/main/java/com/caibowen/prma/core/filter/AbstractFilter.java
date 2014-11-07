package com.caibowen.prma.core.filter;

import com.caibowen.gplume.annotation.NoExcept;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public abstract class AbstractFilter<E> implements Filter<E> {

    protected Filter next;
    protected boolean started;

    @NoExcept
    protected abstract int doAccept(E e);

    @NoExcept
    @Override
    public int accept(E e) {
        int ok = doAccept(e);
        /**
         * if accepted by this filter, ask the next.
         * this is how filter chain is implemented
         */
        if (ok == 1)
            return getNext() != null ? getNext().accept(e) : 1;

        return ok;
    }

    @Override
    public void setNext(Filter ef) {
        next = ef;
    }

    @Override
    public Filter getNext() {
        return next;
    }

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
        return started;
    }
}
