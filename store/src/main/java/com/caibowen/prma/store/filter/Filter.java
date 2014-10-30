package com.caibowen.prma.store.filter;

import com.caibowen.gplume.annotation.NoExcept;
import com.caibowen.gplume.common.URITrie;
import com.caibowen.gplume.misc.Str;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author BowenCai
 * @since 29-10-2014.
 */
public interface Filter<E> {

    @NoExcept
    boolean accept(E e);
    Filter getNext();
    void setNext(Filter ef);

}
