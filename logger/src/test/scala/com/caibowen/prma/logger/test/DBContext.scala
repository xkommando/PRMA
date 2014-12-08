package com.caibowen.prma.logger.test

import javax.sql.DataSource

import com.caibowen.gplume.context.{AppContext, ContextBooter}
import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.store.EventStore
import com.zaxxer.hikari.HikariDataSource

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
class DBContext {

  val manifestPath: String = "classpath:store_assemble.xml"

  val dataSource = connect
  val jdbcSupport = new JdbcSupport(dataSource)
  jdbcSupport.setTraceSQL(false)
  jdbcSupport setDataSource dataSource
  jdbcSupport setTraceSQL false
  AppContext.beanAssembler.addBean("dataSource", dataSource)
  _prepare()

  val eventStore = AppContext.beanAssembler.getBean("eventStore").asInstanceOf[EventStore]

  def connect: DataSource = {
    val ds  = new HikariDataSource
    ds.setAutoCommit(true)
    ds.setMinimumIdle(2)
    ds.setMaximumPoolSize(32)
    ds.setDriverClassName("com.mysql.jdbc.Driver")
    ds.setUsername("bitranger")
    ds.setPassword("123456")
    ds.setJdbcUrl("jdbc:mysql://localhost:3306/prma_log")
    //    ds.getParentLogger.setLevel(java.util.logging.Level.WARNING)
    return ds
  }

  def _prepare() : Unit = {
    val _bootstrap = new ContextBooter
    _bootstrap.setClassLoader(this.getClass.getClassLoader)
    _bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass.getClassLoader))
    _bootstrap.setManifestPath(manifestPath)
    _bootstrap.boot
  }

}
