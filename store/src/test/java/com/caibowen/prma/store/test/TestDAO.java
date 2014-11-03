package com.caibowen.prma.store.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.gplume.jdbc.transaction.JdbcTransactionManager;
import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.gplume.jdbc.transaction.TransactionManager;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.dao.EventDAO;
import com.caibowen.prma.store.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author BowenCai
 * @since 25-10-2014.
 */
public class TestDAO {

    public DataSource connect() {
        HikariDataSource ds = new HikariDataSource();
        ds.setAutoCommit(true);
        ds.setMinimumIdle(5);
        ds.setMaximumPoolSize(10);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("bitranger");
        ds.setPassword("123456");
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/prma_log");

        return ds;
    }

    static final Logger LOGGER = LoggerFactory.getLogger("console-logger");

    DataSource ds;

    EventPersist eventP;
    Int4DAO<String> loggerDAO;
    Int4DAO<String> exceptMsgDAO;
    EventDAO eventDAO;

    TransactionManager manager = new JdbcTransactionManager();

    @Test
    public void s() throws Throwable {
        Throwable _t = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));
        Throwable r = _t.getCause();
        while (r != null) {
            for (StackTraceElement st : r.getStackTrace())
                System.out.println(st.getClassName());
            r = r.getCause();
        }
        throw _t;
    }

    @Before
    public void setup() {
        ContextBooter bootstrap = new ContextBooter();
        bootstrap.setClassLoader(this.getClass().getClassLoader());
        // prepare
        bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass().getClassLoader()));

        String manifestPath = "classpath:store_assemble_test.xml";
        bootstrap.setManifestPath(manifestPath);

        ds = connect();
        AppContext.beanAssembler.addBean("dataSource", ds);

        bootstrap.boot();
        eventP = AppContext.beanAssembler.getBean("eventPersist");
        loggerDAO = AppContext.beanAssembler.getBean("loggerDAO");
        exceptMsgDAO = AppContext.beanAssembler.getBean("exceptMsgDAO");
        eventDAO = AppContext.beanAssembler.getBean("eventDAO");

        manager.setDataSource(ds);
    }

    @Test
    public void commit() {

        String s = "safshferighueirhge";

        Transaction tnx = manager.begin();
        exceptMsgDAO.put(s.hashCode(), s);
        loggerDAO.put(s.hashCode(), s);
        manager.commit(tnx);
        LOGGER.trace("" + tnx.isCompleted());
    }

    @Test
    public void rollback() {

        String s = "safshferighueirhge";

        Transaction tnx = manager.begin();
        exceptMsgDAO.put(s.hashCode(), s);
        loggerDAO.put(s.hashCode(), s);

        manager.rollback(tnx);
        LOGGER.trace(tnx.toString());
        LOGGER.trace("" + tnx.isCompleted());
    }

    @Test
    public void nestedTnx() {

        String s1 = "111111111111111safshferighueirhge";
        String s2 = "2222222222222222222safshferighueirhge";

        Transaction tx1 = manager.begin();
        loggerDAO.put(s1.hashCode(), s1);
                Transaction tx2 = manager.begin();
                loggerDAO.put(s2.hashCode(), s2);
        manager.rollback(tx1);
                exceptMsgDAO.put(s2.hashCode(), s2);
                manager.commit(tx2);

        LOGGER.trace("" + tx1.isCompleted());
        LOGGER.trace("" + tx2.isCompleted());
    }

    @Test
    public void dao_tnx() {
        final Throwable _t = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));

        LoggingEvent le = new LoggingEvent(
                "logger name ??? haha", (ch.qos.logback.classic.Logger)LOGGER, Level.ERROR, "hahaha messsssage", _t, null);

        // once with no inner exception, once with exception from a random procedure.
//        eventP.persist(le);

        final String s1 = "111111111111111safshferighueirhge";
        final String s2 = "2222222222222222222safshferighueirhge";

        Transaction tx1 = manager.begin();
        Transaction tx2 = manager.begin();
        manager.rollback(tx1);
        exceptMsgDAO.put(s2.hashCode(), s2);
        tx2.setRollbackOnly(true);
        manager.commit(tx2);
        try {
            eventDAO.execute(new TransactionCallback<Object>() {
                @Override
                public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                    loggerDAO.put(s1.hashCode(), s1);
                    exceptMsgDAO.put(s1.hashCode(), s1);
                    tnx.setRollbackOnly(true);
                    // operation 2
                    eventDAO.execute(new TransactionCallback<Object>() {
                        @Override
                        public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                            loggerDAO.put(s2.hashCode(), s2);
                            exceptMsgDAO.put(s2.hashCode(), s2);/// did
                            throw new RuntimeException("exp from nested tnx");
//                        return null;
                        }
                    });
                    // fail on any of the operations will trigger rollback automatically
                    return null;
                }
            });
        } catch (Throwable e) {
            System.out.printf(e.getClass().getSimpleName());
        }
    }


    //        System.out.println(eventP);

////        eventP.persist(le);
//        System.out.println(exceptMsgDAO.get("msg level 1".hashCode()));
////        from DB 356721094
//        System.out.println(loggerDAO.get(356721094));


}
