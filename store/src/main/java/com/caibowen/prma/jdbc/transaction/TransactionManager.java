package com.caibowen.prma.jdbc.transaction;

import com.caibowen.prma.jdbc.ConnectionHolder;
import com.caibowen.prma.jdbc.JdbcException;
import com.caibowen.prma.jdbc.JdbcUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author BowenCai
 * @since 28-10-2014.
 */
public class TransactionManager {

    public DataSource dataSource;

    public Transaction begin() throws SQLException {
        return begin(TransactionConfig.DEFAULT);
    }

    public Transaction begin(TransactionConfig config) {

        Transaction tnx = new Transaction();
        tnx.completed = false;

        ConnectionHolder holder = SyncCenter.get(dataSource);
        if (holder == null) {
            try {
                holder = new ConnectionHolder(dataSource.getConnection());
            } catch (SQLException se) {
                throw new JdbcException("Could not get connection for transaction", se);
            }
            SyncCenter.add(dataSource, holder);
            SyncCenter.setSync(true);
            holder.isSync = true;

        } else if (holder.tnxActive && SyncCenter.isSyncActive()) {
        // this holder is already in tnx, start new nested tnx
            try {
                holder = new ConnectionHolder(dataSource.getConnection());
            } catch (SQLException se) {
                throw new JdbcException("Could not get connection for transaction", se);
            }
            holder.isSync = false;
        }

        Connection _con = holder.currentCon;
        try {
            _con.setReadOnly(config.isReadOnly());
            int oldLevel = _con.getTransactionIsolation();
            if (oldLevel != config.getIsolationLevel()) {
                _con.setTransactionIsolation(config.getIsolationLevel());
                tnx.prevISOLevel = oldLevel;
            }
            if (_con.getAutoCommit()) {
                _con.setAutoCommit(false);
                if (config.isResetReadOnly())
                    tnx.resetAutoCommit = true;
            }
        } catch (SQLException se) {
            throw new JdbcException("Could not config connection for transaction", se);
        }

        if (config.getTimeout() != TransactionConfig.DEFAULT_TIMEOUT)
            holder.queryTimeout = config.getTimeout();

        holder.tnxActive = true;
        tnx.holder = holder;
        return tnx;
    }


    public void commit(Transaction tnx) {
        if (tnx.completed)
            throw new IllegalStateException(
                    "Transaction is already completed - do not call commit or rollback more than once per transaction");
        if (tnx.savepoint != null) {
            tnx.releaseSavepoint();
        }
        try {
            tnx.holder.currentCon.commit();
        }
        catch (SQLException ex) {
            // LOGGGG
            rollback(tnx);
            throw new JdbcException("Could not commit JDBC transaction", ex);
        }
        complete(tnx);
    }

    public void rollback(Transaction tnx) {
        if (tnx.completed) {
            throw new IllegalStateException(
                    "Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        if (tnx.savepoint != null)
            tnx.rollbackToSavepoint();
        else
            tnx.rollback();

        complete(tnx);
    }


    private void complete(Transaction tnx) {
        tnx.holder.tnxActive = false;
        Connection con = tnx.holder.currentCon;
        if (con != null) {
            try {
                if (tnx.prevISOLevel != con.getTransactionIsolation())
                    con.setTransactionIsolation(tnx.prevISOLevel);

                if (tnx.resetAutoCommit)
                    con.setAutoCommit(true);

                if (con.isReadOnly())
                    con.setReadOnly(false);

                JdbcUtil.releaseConnection(tnx.holder.currentCon, dataSource);
            } catch (SQLException ex) {
//            logger.debug("Could not close JDBC Connection", ex);
            } catch (Throwable ex) {
//            logger.debug("Unexpected exception on closing JDBC Connection", ex);
            }
//        tnx.completed = true;
        }
    }


}
