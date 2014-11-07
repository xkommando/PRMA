package com.caibowen.prma.core.filter;

/**
 * @author BowenCai
 * @since 29-10-2014.
 */
public class StackTraceFilter extends AbstractFilter<StackTraceElement> {

    @Override
    protected int doAccept(StackTraceElement stackTraceElement) {
        return 0;
    }
}
