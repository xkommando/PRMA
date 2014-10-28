package com.caibowen.prma.jdbc;

import com.caibowen.prma.jdbc.transaction.TransactionConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public class ConnectionHolder {

    public Connection currentCon;
    public boolean isSync = false;
    public boolean tnxActive = false;
    public int queryTimeout = TransactionConfig.DEFAULT_TIMEOUT;
//    private int savepointCount = 0;

    private int refCount = 0;

    public ConnectionHolder(Connection currentCon) {
        this.currentCon = currentCon;
    }

    void addRef() {
        refCount++;
    }

    void deRef() throws SQLException {
        refCount--;
        if (refCount == 0 && currentCon != null) {
            currentCon.close();
            currentCon = null;
        }
    }

    public int countRef() {
        return refCount;
    }


    @Override
    public String toString() {
        return "ConnectionHolder{" +
                "currentCon=" + currentCon +
                ", isSync=" + isSync +
                ", tnxActive=" + tnxActive +
                ", queryTimeout=" + queryTimeout +
                ", refCount=" + refCount +
                '}';
    }
}
