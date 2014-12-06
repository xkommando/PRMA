package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import javax.sql.DataSource

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import com.alibaba.fastjson.JSON
import com.caibowen.gplume.context.{ContextBooter, AppContext}
import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import com.caibowen.prma.store.EventStore
import com.zaxxer.hikari.HikariDataSource
import org.junit.{Before, Test}
import org.slf4j._

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class LogBackTest {
  private[this] var LOG: Logger = LoggerFactory.getLogger(classOf[LogBackTest])

  val adopter = new LogbackEventAdaptor

  val ds = connect

  val jdbc = new JdbcSupport

  val manifestPath: String = "classpath:store_assemble.xml"


  var eventStore: EventStore = _
  @Test
  def adopt: Unit = {
    val exp = new RuntimeException("msg level 3", new IOException("msg level 2", new FileNotFoundException("msg level 1")))


    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")

    val lbEvent = new LoggingEvent("fmt scala logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", exp, null)

    val vo = adopter.from(lbEvent)
    Console.setOut(System.err)
    //    Console.withOut(System.err){}

    println(s"prop ${vo.propertyCount} mk ${vo.markerCount} exp ${vo.exceptionCount} \r\n vo $vo \r\n----\r\n")
    println(JSON.toJSONString(vo.asInstanceOf[AnyRef], true))

    eventStore.put(vo)
    println("done")
  }

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


  @Before
  def setUp: Unit = {
    jdbc setDataSource ds
    jdbc setTraceSQL false

    AppContext.beanAssembler.addBean("dataSource", ds)


    val bootstrap = new ContextBooter
    bootstrap.setClassLoader(this.getClass.getClassLoader)
    // prepare
    bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass.getClassLoader))

    bootstrap.setManifestPath(manifestPath)

    bootstrap.boot

    eventStore = AppContext.beanAssembler.getBean("eventStore")
  }


}
