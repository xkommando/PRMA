package com.caibowen.prma.core.filter;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.core.filter.basic.AbstractFilter;

/**
 * filter eventVo based on
 * level
 * logger name,
 * thread name,
 * exception,
 * message,
 * stacktrace,
 *
 *  etc.
 *
 * @author BowenCai
 * @since 11-11-2014.
 */
public class LogFilter extends AbstractFilter<EventVO> {

    @Override
    protected int doAccept(EventVO eventVO) {
        return 0;
    }
}
