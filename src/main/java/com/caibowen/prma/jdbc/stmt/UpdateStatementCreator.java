package com.caibowen.prma.jdbc.stmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author BowenCai
 *
 * @since 2013-5-6
 */
public interface UpdateStatementCreator extends StatementCreator {

	PreparedStatement createUpdate(Connection con, final String[] cols) throws SQLException;
}
