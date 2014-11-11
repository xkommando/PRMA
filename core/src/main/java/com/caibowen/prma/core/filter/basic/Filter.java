package com.caibowen.prma.core.filter.basic;

import com.caibowen.gplume.annotation.NoExcept;
import com.caibowen.prma.core.LifeCycle;

/**
 * @author BowenCai
 * @since 29-10-2014.
 */
public interface Filter<E> extends LifeCycle {

    /**
     *
     * @param e
     * @return -1 for reject, 1 for accept, 0 for neutral.
     */
    @NoExcept
    int accept(E e);

    Filter getNext();

    void setNext(Filter ef);

}
