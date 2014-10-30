package com.caibowen.prma.store.test;

import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.prma.jdbc.transaction.Transaction;
import com.caibowen.prma.jdbc.transaction.TransactionManager;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

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

    TransactionManager manager = new TransactionManager();

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

        manager.setDataSource(ds);
    }

    @Test
    public void commit() {

        String s = "safshferighueirhge";

        Transaction tnx = manager.begin();
        exceptMsgDAO.put(s.hashCode(), s);
        loggerDAO.put(s.hashCode(), s);
        manager.commit(tnx);
        LOGGER.trace(tnx.toString());
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

        LOGGER.trace(tx1.toString());
        LOGGER.trace("" + tx1.isCompleted());
        LOGGER.trace("" + tx2.isCompleted());
    }

//        System.out.println(eventP);
//
//        Throwable _t = new RuntimeException("msg level 3",
//                new IOException("msg level 2",
//                        new FileNotFoundException("msg level 1")));
//
//        LoggingEvent le = new LoggingEvent(
//                "logger name ???", (ch.qos.logback.classic.Logger)LOGGER, Level.ERROR, "hahaha messsssage", _t, null);
//
////        eventP.xxxxxxxx(le);
//        System.out.println(exceptMsgDAO.get("msg level 1".hashCode()));
////        from DB 356721094
//        System.out.println(loggerDAO.get(356721094));


}
