package com.caibowen.prma.monitor.eval;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.core.filter.basic.StrFilter;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class LoggerNameEval implements Evaluator {

    @Inject StrFilter filter;

    @Override
    public int eval(EventVO vo) {
        return filter.accept(vo.getLoggerName());
    }

}
