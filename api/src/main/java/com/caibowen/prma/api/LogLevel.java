package com.caibowen.prma.api;

/**
 * @author BowenCai
 * @since 26-10-2014.
 */
public enum LogLevel {

    /**
     * The <code>ERROR</code> level designates error events which may or not
     * be fatal to the application.
     */
    ERROR(8, "ERROR"),

    /**
     * The <code>WARN</code> level designates potentially harmful situations.
     */
    WARN(6, "WARN"),

    /**
     * The <code>INFO</code> level designates informational messages
     * highlighting overall progress of the application.
     */
    INFO(4, "INFO"),

    /**
     * The <code>DEBUG</code> level designates informational events of lower
     * importance.
     */
    DEBUG(2, "DEBUG"),

    /**
     * The <code>TRACE</code> level designates informational events of very low
     * importance.
     */
    TRACE(1, "TRACE");

    public final int levelInt;
    public final String levelStr;
    LogLevel(int i, String s) {
        this.levelInt = i;
        this.levelStr = s;
    }

}
