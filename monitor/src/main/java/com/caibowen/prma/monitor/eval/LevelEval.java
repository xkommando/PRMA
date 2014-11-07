package com.caibowen.prma.monitor.eval;

import com.caibowen.prma.api.model.EventVO;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class LevelEval implements Evaluator {

    @Inject int lowerBound = -1;
    @Inject int upperBound = 0;

    @Override
    public int eval(EventVO vo) {

        return 0;
    }
}
