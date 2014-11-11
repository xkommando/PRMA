package com.caibowen.prma.core.filter.basic;

import com.caibowen.prma.api.LogLevel;

/**
 * @author BowenCai
 * @since 11-11-2014.
 */
public class LevelFilter extends AbstractFilter<LogLevel> {


    @Override
    protected int doAccept(LogLevel logLevel) {
        return 0;
    }


}
