package com.caibowen.prma.logger.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.prma.api.EventAdaptor;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.logger.logback.LogbackEventAdaptor;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.rdb.dao.EventDAO;
import com.caibowen.prma.store.rdb.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.*;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BowenCai
 * @since 11-11-2014.
 */
public class ILogEventTest {


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

    static EventAdaptor<ILoggingEvent> translator = new LogbackEventAdaptor();
    DataSource ds;

    EventPersist eventP;
    Int4DAO<String> loggerDAO;
    Int4DAO<String> exceptMsgDAO;
    EventDAO eventDAO;

    JdbcSupport jdbc;

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
        ds = connect();
        AppContext.beanAssembler.addBean("dataSource", ds);
        jdbc = new JdbcSupport(ds);

        ContextBooter bootstrap = new ContextBooter();
        bootstrap.setClassLoader(this.getClass().getClassLoader());
        // prepare
        bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass().getClassLoader()));

        String manifestPath = "classpath:store_assemble.xml";
        bootstrap.setManifestPath(manifestPath);


        bootstrap.boot();
        eventP = AppContext.beanAssembler.getBean("eventPersist");
        loggerDAO = AppContext.beanAssembler.getBean("loggerDAO");
        exceptMsgDAO = AppContext.beanAssembler.getBean("exceptMsgDAO");
        eventDAO = AppContext.beanAssembler.getBean("eventDAO");
    }

    Logger LOG = LoggerFactory.getLogger(ILogEventTest.class);


    @Test
    public void insert2() {
        Throwable _fk = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));

        LoggingEvent e = new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG,
                Level.DEBUG, "hahahaha msg", _fk, null);
        Marker mk1 = MarkerFactory.getMarker("marker 1");
        Marker mk2 = MarkerFactory.getMarker("marker 2");
        mk1.add(mk2);
        e.setMarker(mk1);
        String mdc1 = "test mdc 1";
        MDC.put(mdc1, "hahaha");
        MDC.put("test mdc 2", "hahaha222");
        MDC.put("test mdc 3", "wowowo");

        EventVO vvvo = translator.from(e);
        System.err.println(eventP.persist(vvvo));

        MDC.remove(mdc1);
        e = new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG, Level.TRACE,
                "hahahaha msg 2", null, null);

        vvvo = translator.from(e);
        eventP.persist(vvvo);

    }

    @Test
    public void testadopt() {
        Throwable _fk = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));

        ArrayList<EventVO> vos = new ArrayList<>(16);

        vos.add(translator.from(
                new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG,
                        Level.DEBUG, "hahahaha msg", null, null)
        ));
        Exception _ex = new RuntimeException();
        vos.add(translator.from(
                new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG,
                        Level.DEBUG, "hahahaha msg", _ex, null)
        ));
        LoggingEvent e;
        for (int i = 0; i < 10; i++) {
            e = new LoggingEvent("fq name",
                    (ch.qos.logback.classic.Logger)LOG,
                    Level.DEBUG,
                    "hahahaha msg " + i,
                    _fk, null);
            vos.add(translator.from(e));
        }
        eventP.batchPersist(vos);
java.util.logging.Logger log = java.util.logging.Logger.getLogger("jul test");
        log.log(java.util.logging.Level.FINER, "hahaha", _ex);

    }

    EventVO gen() {
        Throwable _fk = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));


        return translator.from(
                new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG,
                        Level.DEBUG, "hahahaha msg", null, null));

//        return translator.from(
//                new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG,
//                        Level.DEBUG, "hahahaha msg", _fk, null)
//        )
    }
}
