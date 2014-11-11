package com.caibowen.prma.core.filter;

import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.core.filter.basic.AbstractFilter;

/**
 *
 * filter exception based on stacktraces, exception name or message
 *
 * @author BowenCai
 * @since 11-11-2014.
 */
public class ExceptionFilter extends AbstractFilter<ExceptionVO> {

    @Override
    protected int doAccept(ExceptionVO exceptionVO) {
        return 0;
    }
}
