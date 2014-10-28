package com.caibowen.prma.jdbc.transaction;

import com.caibowen.prma.jdbc.ConnectionHolder;
import com.caibowen.prma.jdbc.JdbcException;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * @author BowenCai
 * @since 28-10-2014.
 */
public class Transaction {

    boolean completed = false;

    Savepoint savepoint;

    ConnectionHolder holder;

    int prevISOLevel = TransactionConfig.DEFAULT_ISOLATION;
    boolean resetAutoCommit = true;

    public Savepoint markSavepoint(@Nonnull String name) {
        try {
            return holder.currentCon.setSavepoint(name);
        } catch (SQLException e) {
            throw new JdbcException("Could not create JDBC savepoint", e);
        }
    }

    public void rollbackToSavepoint() {
        rollback(this.savepoint);
    }

    public void rollback(@Nonnull Savepoint sp) {
        try {
            holder.currentCon.rollback(sp);
        } catch (SQLException e) {
            throw new JdbcException("Could not roll back to JDBC savepoint", e);
        }

    }

    public void rollback() {
        try {
            holder.currentCon.rollback();
        } catch (SQLException e) {
            throw new JdbcException("Could not roll back to JDBC savepoint", e);
        }
    }

    public void releaseSavepoint() {
        releaseSavepoint(this.savepoint);
    }

    public void releaseSavepoint(@Nonnull Savepoint sp) {
        try {
            holder.currentCon.releaseSavepoint(sp);
        } catch (SQLException e) {
            throw new JdbcException("Could not explicitly release JDBC savepoint", e);
        }
    }

}
