package com.caibowen.prma.monitor.checker;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.core.filter.StrFilter;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class LoggerNameChecker implements Checker {

    @Inject StrFilter filter;

    @Override
    public int check(EventVO vo) {
        return filter.accept(vo.getLoggerName());
    }

}
