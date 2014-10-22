package com.caibowen.prma.logger.db;

import com.caibowen.prma.common.Pair;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class EventPersister {

    IntDAO<String> threadDAO;
    IntDAO<String> loggerDAO;
    IntDAO<StackTraceElement> stackTraceDAO;
    IntDAO<Pair<String, Object> > propertyDAO;

}
