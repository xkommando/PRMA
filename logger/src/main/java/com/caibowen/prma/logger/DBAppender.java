package com.caibowen.prma.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;

import javax.sql.DataSource;

/**
 * @author BowenCai
 * @since 21-10-2014.
 */
public class DBAppender extends AsyncAppenderWrapper<ILoggingEvent> {


    DataSource dataSource;
//    trace 8
//    DEBUG level is converted to 7,
// INFO is converted to 6,
// WARN is converted to 4
// and ERROR is converted to 3.
/*
caller stack_trace -> id


 */
    void temp() {

    }

    @Override
    protected void passOnEvent(ILoggingEvent iLoggingEvent) {

    }


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
