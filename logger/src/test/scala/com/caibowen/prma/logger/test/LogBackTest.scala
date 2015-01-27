package com.caibowen.prma.logger.test

import java.io.{WriteAbortedException, FileNotFoundException, IOException}
import java.util.concurrent.atomic.{AtomicLong, AtomicInteger}
import java.util.regex.Pattern
import javax.sql.DataSource

import akka.actor.{ActorRef, ActorPath}
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.gplume.jdbc.JdbcSupport
import gplume.scala.context.AppContext
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.logger.logback.{FilteredAdaptor, LogbackEventAdaptor}
import org.junit.Test
import org.slf4j._

import scala.util.Random

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
object LogBackTest {

  val LOG: Logger = LoggerFactory.getLogger(classOf[LogBackTest])
  val counter = new AtomicLong(0)
  val rand = new Random()
  val fadapt = new LogbackEventAdaptor

  def gen = {

    val exp0 = new FileNotFoundException("msg level 0")
    val exp1 = new WriteAbortedException("msg level 1", exp0)
    val exp2 = new IOException("msg level 2", exp1)
    val exp3 = new RuntimeException("msg level 3", exp2)
    val exps = Array(
      exp0,
      exp1,
      exp1,
      exp1,
      null,
      null,
      null,
      null,
      exp1,
      null,
      null,
      null,
      exp2,
      null,
      exp3,
      null
    )

    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")
    if (rand.nextBoolean())
        MDC.clear()

    val eex = exps(rand.nextInt(5))
    val id = counter.getAndIncrement
    val lbEvent = new LoggingEvent("fmt scala logging store test " + id,
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      id +  "scala logging store test", null, null)


    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    if (rand.nextBoolean())
      lbEvent.setMarker(mk1)
//    val _filter = AppContext.beanAssembler.getBean("classNameFilter").asInstanceOf[StrFilter]

    val vo = fadapt.from(lbEvent)
    vo.copy(id = id)
  }
}
class LogBackTest extends BuildContext {

  val dataSource = AppContext.beanAssembler.getBean("dataSource").asInstanceOf[DataSource]
  val jdbcSupport = new JdbcSupport(dataSource)
  jdbcSupport setTraceSQL false

  val eventStore = AppContext.beanAssembler.getBean("eventStore").asInstanceOf[ActorRef]


  val adopter = new LogbackEventAdaptor

  def actorName: Unit = {
    val actorNamePattern: Pattern = ActorPath.ElementRegex.pattern
    println(actorNamePattern.matcher("PRMA.Monitor.Actor_No.1").matches())
    println(actorNamePattern.matcher("PRMA.Store.Actor").matches())
  }

  @Test
  def adopt: Unit = {

    val exp = new RuntimeException("msg level 3", new IOException("msg level 2"))//, new FileNotFoundException("msg level 1")))

    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")
//    MDC.clear()
    val lbEvent = new LoggingEvent("fmt scala logging store test",
  LogBackTest.LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", exp, null)
    lbEvent.setMarker(mk1)
    val _filter = AppContext.beanAssembler.getBean("classNameFilter").asInstanceOf[StrFilter]

    val fadapt = new FilteredAdaptor(_filter, _filter)
    val vo = fadapt.from(lbEvent)
    Console.setOut(System.err)

    eventStore ! vo

  }


}


//case object StackTraceElemtSerializer[StactTraceElement](
//{
//case JString(s) =>  LogLevel.from(s)
//},
//{
//case d: LogLevel.LogLevel => JString(d.toString)
//}
//))
//)