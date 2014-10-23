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

import com.caibowen.prma.jdbc.mapper.*;
import com.caibowen.prma.jdbc.stmt.StatementCreator;
import com.caibowen.prma.jdbc.stmt.UpdateStatementCreator;

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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        try {
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
            boolean out = st.execute();
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState() + "\nCaused by\n"
                    + e.getCause(), e);
        }
    }

    @Override
    public boolean execute(final String sql) {
        try {
            Connection connection = getConnection();
            Statement st = connection.createStatement();
            boolean out = st.execute(sql);
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState() + "\nCaused by\n"
                    + e.getCause(), e);
        }
    }

    @Override
    public boolean execute(final String sql, final Object... params) {
        return execute(getStatementCreator(sql, params));
    }

//-----------------------------------------------------------------------------
//						update delete
//-----------------------------------------------------------------------------

    /**
     * Base function
     */
    @Override
    public int update(StatementCreator psc) {
        try {
            Connection connection = getConnection();
            PreparedStatement st = psc.createStatement(connection);
            int out = st.executeUpdate();
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }


    /**
     * and 4 Wrappers
     */
    @Override
    public int update(final String sql) {
        try {
            Connection connection = getConnection();
            Statement st = connection.createStatement();
            int out = st.executeUpdate(sql);
            JdbcUtil.closeStatement(st);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }

    @Override
    public int update(final String sql, final Object... params) {

        return update(getStatementCreator(sql, params));
    }


    @Override
    public int[] batchUpdate(String... sql) {
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            for (String string : sql) {
                stmt.addBatch(string);
            }
            int[] out = stmt.executeBatch();
            JdbcUtil.closeStatement(stmt);
            JdbcUtil.closeConnection(connection);
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
    }

//-----------------------------------------------------------------------------
//						insert
//-----------------------------------------------------------------------------

    @Override
    public <T> Map<String, Object> insert(final T obj, ObjectMapping<T> mapper, final String[] cols) {
        return insert(mapper.insert(obj), cols, new MapExtractor(cols));
    }



    @Override
    public <T> T insert(UpdateStatementCreator psc, final String[] cols, RowMapping<T> resultExtract) {
        try {
            Connection connection = getConnection();

            PreparedStatement ps = psc.createUpdate(connection, cols);
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

    /**
     * @return Map<String, T> is the getGenerated keys
     *
     *  example:
     *
     *  keys =insert(entity, new String[]{"id", "time_created"})
     *
     *  entityId = keys.get("id");
     *
     */
    @Override
    public Map<String, Object> insert(final String sql, String[] cols) {

        return insert(new UpdateStatementCreator() {

            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException { throw new UnsupportedOperationException(); }

            @Override
            public PreparedStatement createUpdate(Connection con, String[] cols)
                    throws SQLException {
                PreparedStatement ps =con.prepareStatement(sql, cols);
                return ps;
            }
        }, cols, new MapExtractor(cols));
    }

    @Override
    public Map<String, Object> insert(final String sql,
                                      String[] cols,
                                      final Object... params) {

        return insert(new UpdateStatementCreator() {

            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {return null;}

            @Override
            public PreparedStatement createUpdate(Connection con, String[] cols)
                    throws SQLException {
                PreparedStatement ps =con.prepareStatement(sql, cols);
                setParams(ps, params);
                return ps;
            }
        }, cols, new MapExtractor(cols));
    }
//-----------------------------------------------------------------------------
//	select
//-----------------------------------------------------------------------------

//-----------------------------------------------
//	get object by appointed type
//-----------------------------------------------

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
    public <T> T queryForObject(StatementCreator psc, RowMapping<T> mapper) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = psc.createStatement(connection);
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
//-----------------------------------------------------------------------------
//						just like queryForObject 
//-----------------------------------------------------------------------------

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


    @SuppressWarnings("resource")
    @Override
    public <T> List<T> queryForList(StatementCreator psc, RowMapping<T> mapper) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = psc.createStatement(connection);
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

    @Override
    public Map<String, Object> queryForMap(String sql) {
        ColumnMapper mapper = new ColumnMapper();
        return queryForObject(sql, mapper);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... params) {
        ColumnMapper mapper = new ColumnMapper();
        return queryForObject(sql, mapper, params);
    }


    @Override
    public Map<String, Object> queryForMap(StatementCreator psc) {
        ColumnMapper mapper = new ColumnMapper();
        return queryForObject(psc, mapper);
    }

    @Override
    public ResultSet queryForResultSet(String sql) {

        Connection connection = getConnection();
        ResultSet rs;
        try {
            rs = connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
        return rs;
    }

    @Override
    public ResultSet queryForResultSet(final String sql, final Object... params) {

        return queryForResultSet(new StatementCreator() {

            @Override
            public PreparedStatement createStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                setParams(ps, params);
                return ps;
            }
        });
    }

    @Override
    public ResultSet queryForResultSet(StatementCreator psc) {

        Connection connection = getConnection();
        ResultSet rs;
        try {
            rs = psc.createStatement(connection).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState(), e);
        }
        return rs;
    }

    //-----------------------------------------------------------------------------
    public static void setParams(PreparedStatement ps, Object... params) throws SQLException {

        if (ps != null && params != null) {
            for (int i = 0; i != params.length; ++i) {
                ps.setObject(i + 1, params[i]);
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


