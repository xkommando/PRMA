package com.caibowen.prma.monitor.checker;

import com.caibowen.prma.api.model.EventVO;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public interface Checker {

    int check(EventVO vo);
}
