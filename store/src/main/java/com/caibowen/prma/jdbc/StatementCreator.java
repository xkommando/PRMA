package com.caibowen.prma.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 *
 * @author BowenCai
 *
 * @since 2013-5-6
 */
public interface StatementCreator {

	PreparedStatement createStatement(Connection con) throws SQLException;
}
