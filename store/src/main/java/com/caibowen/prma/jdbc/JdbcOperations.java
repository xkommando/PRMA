/*******************************************************************************
 * Copyright (c) 2013 Bowen Cai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Bowen Cai - initial API and implementation
 ******************************************************************************/
package com.caibowen.prma.jdbc;

import com.caibowen.prma.jdbc.mapper.RowMapping;

import java.util.List;
import java.util.Map;

/**
 *
 * @author BowenCai
 *
 * @since 2013-5-6
 */
public interface JdbcOperations {

	boolean execute(final String sql);
    boolean execute(final String sql, final Object... params);
	boolean execute(StatementCreator creator);
    int[] batchExecute(StatementCreator creator);

	int update(final String sql);
    int[] batchUpdate(final String... sql);
	int update(final String sql, Object... params);
	int update(StatementCreator creator);
    int[] batchUpdate(StatementCreator creator);

	Map<String, Object> insert(final String sql, final String[] cols);
	Map<String, Object> insert(final String sql, final String[] cols, Object... params);
    <T> T insert(StatementCreator creator, final String[] cols, RowMapping<T> resultExtract);
    List<Map<String, Object>> batchInsert(String[] sqls, String[] cols);
    <T> List<T> batchInsert(StatementCreator creator, final String cols[], RowMapping<T> extractor);


	<T> T queryForObject(final String sql, Class<T> type);
	<T> T queryForObject(final String sql, Class<T> type, final Object... params);
	<T> T queryForObject(final StatementCreator psc, Class<T> type);
	
	<T> T queryForObject(final String sql, RowMapping<T> mapper);
	<T> T queryForObject(final String sql, RowMapping<T> mapper, Object... params);
	<T> T queryForObject(final StatementCreator psc, RowMapping<T> mapper);
	
	<T> List<T> queryForList(final String sql, Class<T> type);
	<T> List<T> queryForList(final String sql, Class<T> type, final Object... params);
	<T> List<T> queryForList(final StatementCreator psc, Class<T> type);
	
	<T> List<T> queryForList(final String sql, RowMapping<T> mapper);
	<T> List<T> queryForList(final String sql, RowMapping<T> mapper, Object... params);
	<T> List<T> queryForList(final StatementCreator psc, RowMapping<T> mapper);

    List<Map<String, Object>> queryForList(final String sql);
    List<Map<String, Object>> queryForList(final String sql, Object... params);
    List<Map<String, Object>> queryForList(final StatementCreator psc);


	Map<String, Object> queryForMap(final String sql);
	Map<String, Object> queryForMap(final String sql, Object... params);
	Map<String, Object> queryForMap(final StatementCreator psc);

	
//	ResultSet queryForResultSet(final String sql);
//	ResultSet queryForResultSet(final String sql, Object... params);
//	ResultSet queryForResultSet(final StatementCreator psc);
	
}