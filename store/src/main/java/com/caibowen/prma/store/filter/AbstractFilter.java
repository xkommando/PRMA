package com.caibowen.prma.store.filter;

import com.caibowen.gplume.annotation.NoExcept;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public abstract class AbstractFilter<E> implements Filter<E> {


    public abstract void init();

    @NoExcept
    protected abstract boolean doAccept(E e);

    @NoExcept
    @Override
    public boolean accept(E e) {
        boolean ok = doAccept(e);
        /**
         * if accepted by this filter, ask the next.
         * this is how filter chain is implemented
         */
        if (ok)
            return getNext() != null ? getNext().accept(e) : true;

        return false;
    }

    private Filter next;
    @Override
    public void setNext(Filter ef) {
        next = ef;
    }

    @Override
    public Filter getNext() {
        return next;
    }

}
