package com.caibowen.prma.store.test;

import com.caibowen.gplume.common.collection.ImmutableArraySet;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.transaction.JdbcTransactionManager;
import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.gplume.jdbc.transaction.TransactionManager;
import com.caibowen.prma.api.LogLevel;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.rdb.dao.EventDAO;
import com.caibowen.prma.store.rdb.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import javax.tools.JavaCompiler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BowenCai
 * @since 25-10-2014.
 */
public class TestStore {

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
    JdbcSupport jdbc;
    TransactionManager manager = new JdbcTransactionManager();

    ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 256, 60L, TimeUnit.SECONDS,
                                                                                        new SynchronousQueue<Runnable>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(final Runnable r, ThreadPoolExecutor executor) {
            class _R implements Runnable {
                @Override
                public void run() {
                    r.run();
                }
            }
            if (r instanceof _R)
                r.run();
            else
                executor.execute(new _R());
        }
    });


    @Before
    public void setup() {
        ContextBooter bootstrap = new ContextBooter();
        bootstrap.setClassLoader(this.getClass().getClassLoader());
        // prepare
        bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass().getClassLoader()));

        String manifestPath = "classpath:store_assemble.xml";
        bootstrap.setManifestPath(manifestPath);

        ds = connect();
        AppContext.beanAssembler.addBean("dataSource", ds);

        bootstrap.boot();
        eventP = AppContext.beanAssembler.getBean("eventPersist");
        loggerDAO = AppContext.beanAssembler.getBean("loggerDAO");
        exceptMsgDAO = AppContext.beanAssembler.getBean("exceptMsgDAO");
        eventDAO = AppContext.beanAssembler.getBean("eventDAO");
        jdbc = new JdbcSupport(ds);
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

        // once with no inner exception, once with exception to a random procedure.
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
            jdbc.execute(new TransactionCallback<Object>() {
                @Override
                public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                    loggerDAO.put(s1.hashCode(), s1);
                    exceptMsgDAO.put(s1.hashCode(), s1);
                    tnx.setRollbackOnly(true);
                    // operation 2
                    jdbc.execute(new TransactionCallback<Object>() {
                        @Override
                        public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                            loggerDAO.put(s2.hashCode(), s2);
                            exceptMsgDAO.put(s2.hashCode(), s2);/// did
                            throw new RuntimeException("exp to nested tnx");
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
//        System.out.println(exceptMsgDAO.at("msg level 1".hashCode()));
////        to DB 356721094
//        System.out.println(loggerDAO.at(356721094));


//    @Test
//    public void batchInsert() {
//        System.out.println("enter batch");
//
//        List<EventVO> eventls = new ArrayList<>(16);
//
//        Throwable[] _fk = new Throwable[]{new RuntimeException("msg level 3",
//                new IOException("msg level 2",
//                        new FileNotFoundException("msg level 1"))), new IOException("msg level 1")};
//
//        long t1 = System.currentTimeMillis();
//        StackTraceElement s = (new Throwable().getStackTrace()[0]);
//
//        final ArrayList<ExceptionVO> vols = new ArrayList<ExceptionVO>(16);
//
//        for (Throwable t : _fk) {
//            scala.collection.immutable.List ls;
//            vols.add(new ExceptionVO(t.getClass().getName(), t.getMessage(), t.getStackTrace()));
//        }
//        Set<String> mks = new ImmutableArraySet<>(new Object[]{"marker1", "marker2", "mk3"});
//
//        for (int i = 0; i < 20; i++) {
//            eventls.add(new EventVO(
//                    System.currentTimeMillis(),
//                    LogLevel.DEBUG(), 0L,
//                    "logger name ??? haha " + i,
//                    Thread.currentThread().getName(),
//                    s,
//                    "hahaha messsssage " + i,
//                    -1L,
//                    null, vols, mks));
//        }
//
//        eventP.batchPersist(eventls);
//        long tt = System.currentTimeMillis();
//        System.out.println("Time: " + (tt - t1));
//    }
//    new FileNotFoundException("msg level 1")));

//    LoggingEvent le = new LoggingEvent(
//            "logger name ??? haha", (ch.qos.logback.classic.Logger)LOGGER, Level.ERROR, "hahaha messsssage", _t, null);

//    @Test
//    public void singleInsert() {
//        List<EventVO> eventls = new ArrayList<>(16);
//
//        final Throwable _t = new RuntimeException("msg level 3",
//                new IOException("msg level 2",
//                        new FileNotFoundException("msg level 1")));
//
//        long t = System.currentTimeMillis();
//        for (int i = 0; i < 20; i++) {
//            eventP.persist(new EventVO(
//                    System.currentTimeMillis(),
//                    LogLevel.ERROR,
//                    "logger name ??? haha " + i,
//                    Thread.currentThread().getName(),
//                    new Throwable().getStackTrace()[0],
//                    "hahaha messsssage " + i,
//                    null,
//                    new Throwable[]{_t})
//            );
//        }
//        long tt = System.currentTimeMillis();
//        System.out.println(tt - t);
//    }

    @Test
    public void async() throws Throwable {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("=====================");
            }
        });
        long t = System.currentTimeMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                batchInsert();
            }
        });
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        long tt = System.currentTimeMillis();
        System.out.println(tt - t);
    }


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
}
