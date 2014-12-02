package com.caibowen.prma.store.test;

import com.alibaba.fastjson.JSON;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.rdb.dao.EventDAO;
import com.caibowen.prma.store.rdb.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BowenCai
 * @since 14-11-2014.
 */
public class TestQuery {

    @Test
    public void q1() {
//        System.err.println(JSON.toJSONString(eventP.get(363L), true));
    }

    @Test
    public void q2() {
//        List<EventVO> ls = eventP.getWithException(0, 100);
//        for (EventVO o : ls)
//            System.err.println(JSON.toJSONString(o, true));
    }

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

    Logger LOG = LoggerFactory.getLogger(TestQuery.class);




}
