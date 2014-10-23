package com.caibowen.prma.logger.dao;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.prma.logger.EventPO;


/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface EventDAO {

    long insert(EventPO po);
}
