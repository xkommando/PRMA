package com.caibowen.prma.jdbc.transaction;

/**
 * @author BowenCai
 * @since 28-10-2014.
 */
public interface TransactionCallback<T> {

    T doInTransaction(Transaction tnx) throws Exception;

}
