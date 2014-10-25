package com.caibowen.prma.store.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.caibowen.gplume.context.AppContext;
import com.caibowen.gplume.context.ClassLoaderInputStreamProvider;
import com.caibowen.gplume.context.ContextBooter;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.dao.Int4DAO;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static final Logger LOGGER = LoggerFactory.getLogger("parent logger");

    @Test
    public void up() {
        ContextBooter bootstrap = new ContextBooter();
        bootstrap.setClassLoader(this.getClass().getClassLoader());
        // prepare
        bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass().getClassLoader()));

        String manifestPath = "classpath:store_assemble_test.xml";
        bootstrap.setManifestPath(manifestPath);

        AppContext.beanAssembler.addBean("dataSource", connect());

        bootstrap.boot();
        EventPersist eventP = AppContext.beanAssembler.getBean("eventPersist");
        Int4DAO<String> loggerDAO = AppContext.beanAssembler.getBean("loggerDAO");
        Int4DAO<String> exceptMsgDAO = AppContext.beanAssembler.getBean("exceptMsgDAO");
        System.out.println(eventP);

        Throwable _t = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                        new FileNotFoundException("msg level 1")));

        LoggingEvent le = new LoggingEvent(
                "logger name ???", (ch.qos.logback.classic.Logger)LOGGER, Level.ERROR, "hahaha messsssage", _t, null);

//        eventP.xxxxxxxx(le);
        System.out.println(exceptMsgDAO.get("msg level 1".hashCode()));
//        from DB 356721094
        System.out.println(loggerDAO.get(356721094));
    }
}
