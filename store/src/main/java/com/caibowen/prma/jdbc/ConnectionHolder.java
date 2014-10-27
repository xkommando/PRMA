package com.caibowen.prma.jdbc;

import java.sql.Connection;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public class ConnectionHolder {

    private Connection currentCon;

    private int savepointCount = 0;

}
