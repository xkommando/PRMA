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

import com.caibowen.prma.jdbc.callback.StatementCreator;
import com.caibowen.prma.jdbc.mapper.ColumnMapper;
import com.caibowen.prma.jdbc.mapper.MapExtractor;
import com.caibowen.prma.jdbc.mapper.RowMapping;
import com.caibowen.prma.jdbc.mapper.SingleColumMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author BowenCai
 *
 * @since 2013-5-6
 */
public class JdbcAux implements JdbcOperations {

    public JdbcAux() {
    }

    public JdbcAux(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//-----------------------------------------------------------------------------

    private DataSource dataSource;
    private int queryTimeOut;
    private int maxRow = 0;
    private int fetchSize = 0;

    public int getFetchSize() {
        return fetchSize;
    }
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
    public int getMaxRow() {
        return maxRow;
    }
    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }
    public int getQueryTimeOut() {
        return queryTimeOut;
    }
    public void setQueryTimeOut(int queryTimeOut) {
        this.queryTimeOut = queryTimeOut;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    private void configStatement(Statement st) throws SQLException{
        if (maxRow > 0)
            st.setMaxRows(maxRow);
        if (fetchSize > 0)
            st.setFetchSize(fetchSize);

        // ???

    }


    public Connection getConnection() {
        try {
            ConnectionHolder holder = (ConnectionHolder)SyncCenter.get(dataSource);
            if (holder == null) {
                holder = new ConnectionHoler();
            }
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }

//-----------------------------------------------------------------------------
//						execute
//-----------------------------------------------------------------------------

    /**
     * the base function
     */
    @Override
    public boolean execute(StatementCreator psc) {
        try {
            Connection connection = getConnection();
            PreparedStatement st = psc.createStatement(connection);
            configStatement(st);
            boolean out = st.execute();
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }

    @Override
    public int[] batchExecute(StatementCreator creator) {
        try {
            Connection connection = getConnection();
            Statement st = creator.createStatement(connection);
//            configStatement(st);
            int[] out = st.executeBatch();
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }




    @Override
    public <T> T insert(StatementCreator psc, final String[] cols, RowMapping<T> resultExtract) {
        try {
            Connection connection = getConnection();

            PreparedStatement ps = psc.createStatement(connection);
            configStatement(ps);
            ps.execute();

            T ret = null;
            ResultSet rs = null;

            if (cols != null) {
                rs = ps.getGeneratedKeys();
                if (rs.next())
                    ret = resultExtract.extract(rs);
                JdbcUtil.closeResultSet(rs);
            }
            JdbcUtil.closeStatement(ps);
            JdbcUtil.closeConnection(connection);

            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(),e);
        }
    }

    @Override
    public  <T> List<T> batchInsert(StatementCreator creator, final String cols[], RowMapping<T> extractor) {
        try {
            Connection connection = getConnection();

            PreparedStatement ps = creator.createStatement(connection);
            ps.executeBatch();

            List<T> ret = null;
            ResultSet rs = null;

            if (cols != null) {
                rs = ps.getGeneratedKeys();
                ret = new ArrayList<>(8);
                while (rs.next())
                    ret.add(extractor.extract(rs));

                JdbcUtil.closeResultSet(rs);
            }
            JdbcUtil.closeStatement(ps);
            JdbcUtil.closeConnection(connection);

            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(),e);
        }
    }

    @Override
    public <T> T queryForObject(StatementCreator psc, RowMapping<T> mapper) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = psc.createStatement(connection);
            configStatement(ps);
            ResultSet rs = ps.executeQuery();
            T o = null;
            if (rs.next()) {
                o = mapper.extract(rs);
                assert (rs.isLast());
            }
            JdbcUtil.closeResultSet(rs);
            JdbcUtil.closeStatement(ps);
            JdbcUtil.closeConnection(connection);
            return o;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState()
                    + "\nCaused by\n"
                    + e.getCause(), e);
        }
    }

    @Override
    public <T> List<T> queryForList(StatementCreator psc, RowMapping<T> mapper) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = psc.createStatement(connection);
            configStatement(ps);
            ResultSet rs = ps.executeQuery();
            List<T> ls = new ArrayList<>(8);
            while (rs.next()) {
                ls.add(mapper.extract(rs));
            }
            JdbcUtil.closeResultSet(rs);
            JdbcUtil.closeStatement(ps);
            JdbcUtil.closeConnection(connection);
            return ls;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }

//    @Override
//    public int [] batchExecute(String[] sqls) {
//        try {
//            Connection connection = getConnection();
//            Statement stmt = connection.createStatement();
//            for (String string : sqls) {
//                stmt.addBatch(string);
//            }
////            configStatement(st);
//            int[] out = stmt.executeBatch();
//            JdbcUtil.closeStatement(stmt);
//            JdbcUtil.closeConnection(connection);
//            return out;
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getSQLState(), e);
//        }
//    }

    //    @Override
//    public List<Map<String, Object>> batchInsert(String[] sqls, String[] cols) {
//        try {
//            Connection con = getConnection();
//            Statement st = con.createStatement();
//            for (String s : sqls)
//                st.addBatch(s);
//
//            st.executeBatch();
//
//            List<Map<String, Object>> ret = null;
//            ResultSet rs = null;
//
//            if (cols != null) {
//                rs = st.getGeneratedKeys();
//                ret = new ArrayList<>(8);
//                while (rs.next())
//                    ret.add(COL_MAPPER.extract(rs));
//
//                JdbcUtil.closeResultSet(rs);
//            }
//            JdbcUtil.closeStatement(st);
//            JdbcUtil.closeConnection(con);
//
//            return ret;
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getSQLState(),e);
//        }
//    }

    /**
     * @return Map<String, T> is the getGenerated keys
     *
     *  example:
     *
     *  keys =insertAll(entity, new String[]{"id", "time_created"})
     *
     *  entityId = keys.get("id");
     *
     */
    @Override
    public Map<String, Object> insert(final String sql, final String[] cols) {

        return insert(new StatementCreator() {

            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {
                return cols == null ? con.prepareStatement(sql)
                        : con.prepareStatement(sql, cols);
            }
        }, cols, new MapExtractor(cols));
    }

    @Override
    public Map<String, Object> insert(final String sql,
                                      final String[] cols,
                                      final Object... params) {
        return insert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps =con.prepareStatement(sql, cols);
                setParams(ps, params);
                return ps;}
        }, cols, new MapExtractor(cols));
    }

    @Override
    public List<Map<String, Object>> batchInsert(StatementCreator creator, String[] cols) {
        return batchInsert(creator, cols, COL_MAPPER);
    }




    @Override
    public boolean execute(final String sql) {
        return execute(getStatementCreator(sql));
    }

    @Override
    public boolean execute(final String sql, final Object... params) {
        return execute(getStatementCreator(sql, params));
    }


    /**
     * get object of requested type
     */
    @Override
    public <T> T queryForObject(final String sql, Class<T> type) {
        return queryForObject(getStatementCreator(sql), type);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> type, Object... params) {
        return queryForObject(getStatementCreator(sql, params), type);
    }


    /**
     * get single object of requested type
     */
    @Override
    public <T> T queryForObject(StatementCreator psc, Class<T> type) {
        return queryForObject(psc, new SingleColumMapper<>(type));
    }

//-----------------------------------------------
//	get object by RowMapper
//-----------------------------------------------

    @Override
    public <T> T queryForObject(final String sql, RowMapping<T> mapper) {
        return queryForObject(getStatementCreator(sql), mapper);
    }

    @Override
    public <T> T queryForObject(final String sql,
                                RowMapping<T> mapper,
                                final Object... params) {

        return queryForObject(getStatementCreator(sql, params), mapper);
    }

    @Override
    public <T> List<T> queryForList(final String sql, RowMapping<T> mapper) {

        return queryForList(getStatementCreator(sql), mapper);
    }

    @Override
    public <T> List<T> queryForList(final String sql,
                                    RowMapping<T> mapper,
                                    final Object... params) {

        return queryForList(getStatementCreator(sql, params), mapper);
    }



//-----------------------------------------------
//	get object by type
//-----------------------------------------------

    @Override
    public <T> List<T> queryForList(final String sql, Class<T> type) {
        return queryForList(getStatementCreator(sql), type);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> type, Object... params) {
        return queryForList(getStatementCreator(sql, params), type);
    }


    @Override
    public <T> List<T> queryForList(StatementCreator psc, Class<T> type) {
        return queryForList(psc, new SingleColumMapper<>(type));
    }


//-----------------------------------------------------------------------------
//						Map
//-----------------------------------------------------------------------------

    public static final RowMapping<Map<String, Object>> COL_MAPPER = new ColumnMapper();

    @Override
    public List<Map<String, Object>> queryForList(final String sql) {
        return queryForList(getStatementCreator(sql), COL_MAPPER);
    }
    @Override
    public List<Map<String, Object>> queryForList(final String sql, Object... params) {
        return queryForList(getStatementCreator(sql, params), COL_MAPPER);
    }
    @Override
    public List<Map<String, Object>> queryForList(final StatementCreator psc) {
        return queryForList(psc, COL_MAPPER);
    }

    @Override
    public Map<String, Object> queryForMap(String sql) {
        return queryForObject(sql, COL_MAPPER);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... params) {
        return queryForObject(sql, COL_MAPPER, params);
    }


    @Override
    public Map<String, Object> queryForMap(StatementCreator psc) {
        return queryForObject(psc, COL_MAPPER);
    }

    //-----------------------------------------------------------------------------
    public static void setParams(PreparedStatement ps, Object... params) throws SQLException {

        if (ps != null && params != null) {
            for (int i = 0; i != params.length; ++i) {
                Object val = params[i];
                if (val != null)
                    ps.setObject(i + 1, params[i]);
                else {
                    int type = ps.getParameterMetaData().getParameterType(i + i);
                    ps.setNull(i + 1, type);
                }
            }
        }
    }

//-----------------------------------------------------------------------------
//						private functions
//-----------------------------------------------------------------------------

    /**
     * create a StatementCreator out of a String
     *
     * @param sql
     * @return
     */
    protected static StatementCreator getStatementCreator(final String sql) {
        return new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {
                return con.prepareStatement(sql);
            }
        };
    }

    /**
     * create a StatementCreator out of a String and its parameters
     *
     * @param sql
     * @param params
     * @return
     */
    protected static StatementCreator getStatementCreator(final String sql, final Object... params) {
        return new StatementCreator() {

            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                setParams(ps, params);
                return ps;
            }
        };
    }


}


