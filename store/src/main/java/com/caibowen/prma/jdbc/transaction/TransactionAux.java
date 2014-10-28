package com.caibowen.prma.jdbc.transaction;

import com.caibowen.prma.jdbc.JdbcException;

import java.sql.SQLException;

/**
 * @author BowenCai
 * @since 28-10-2014.
 */
public class TransactionAux {

    private TransactionManager transactionManager;

    public <T> T execute(TransactionConfig cfg, TransactionCallback<T> operations) {
        Transaction tnx = transactionManager.begin(cfg);
        T ret = null;
        try {
            ret = operations.doInTransaction(tnx);
        } catch (SQLException se) {
            transactionManager.rollback(tnx);
            throw new JdbcException(se);
        } catch (Exception e) {
            transactionManager.rollback(tnx);
            throw new JdbcException(e);
        }
        transactionManager.commit(tnx);
        return ret;
    }

}
