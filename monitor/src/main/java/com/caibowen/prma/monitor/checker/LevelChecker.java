package com.caibowen.prma.monitor.checker;

import com.caibowen.prma.api.model.EventVO;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public class LevelChecker implements Checker {

    @Inject int lowerBound = -1;
    @Inject int upperBound = 0;

    @Override
    public int check(EventVO vo) {

        return 0;
    }
}
