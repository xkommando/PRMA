package com.caibowen.prma.logger.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.gplume.jdbc.transaction.JdbcTransactionManager;
import com.caibowen.gplume.jdbc.transaction.TransactionManager;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.logger.logback.EventAdapter;
import com.caibowen.prma.spi.EventAdaptor;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.dao.EventDAO;
import com.caibowen.prma.store.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    DataSource ds;

    EventPersist eventP;
    Int4DAO<String> loggerDAO;
    Int4DAO<String> exceptMsgDAO;
    EventDAO eventDAO;

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

        manager.setDataSource(ds);
    }

    Logger LOG = LoggerFactory.getLogger(ILogEventTest.class);

    @Test
    public void testadopt() {
        Throwable _fk = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));

        EventAdaptor<ILoggingEvent> translator = new EventAdapter();
        ILoggingEvent e = new LoggingEvent("fq name", (ch.qos.logback.classic.Logger)LOG, Level.DEBUG, "hahahaha msg", _fk, null);

        eventP.persist(translator.from(e));

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

        for (int i = 0; i < 10; i++) {
            e = new LoggingEvent("fq name",
                    (ch.qos.logback.classic.Logger)LOG,
                    Level.DEBUG,
                    "hahahaha msg " + i,
                    _fk, null);
            vos.add(translator.from(e));
        }

        eventP.batchPersist(vos);
    }

}
