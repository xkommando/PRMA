package com.caibowen.prma.webface.test

import java.io.IOException

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import com.caibowen.prma.webface.{JsonViewResolver, JsonResult, SearchEngine}
import com.caibowen.prma.webface.controller.HttpQuery
import gplume.scala.context.AppContext
import net.liftweb.json.{DefaultFormats, Serialization}
import org.junit.Test
import org.slf4j.{LoggerFactory, Logger, MDC, MarkerFactory}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by Bowen Cai on 12/26/2014.
 */
class TestWidget {


  @Test
  def stringify(): Unit = {
    val q = new HttpQuery()
    q.maxTime = Long.MaxValue
    q.minTime = Long.MinValue
    q.lowLevel = LogLevel.DEBUG.toString
    q.highLevel = LogLevel.FATAL.toString
    q.exceptionOnly = true
    q.loggerName =
      """
         蔡博文
      """
    q.threadName = "thrradsaf name"
    q.message =
      """
         "name" is 'msg'
      """
    q.fuzzyQuery = true;
    q.exceptionOnly = false;
    val s = new SearchEngine(null).process(q)
    println(s)
    println(s.length)
  }

  def sss(jsr:JsonResult[_]): String ={
    val w = new StringBuilder

    val evls = jsr.data.asInstanceOf[List[EventVO]]
    w.append("{\r\n\"code\":").append(jsr.code)
    if (jsr.message.isDefined)
      w.append(",\r\n\"message\":\"").append(jsr.message.get).append('\"')

    w.append(",\r\n\"data\":[\r\n")

    evls.take(evls.size - 1).foreach(ev => w.append(ev.toString).append('\n').append(','))
    w.append(evls.last.toString).append(']')

    w.append("\r\n}").toString
  }

  val resolver = new JsonViewResolver
  @Test
  def testResolve: Unit ={
    val result = new JsonResult(Some(List(
    gen(true, false),
//    gen(false, true),
    gen(false, false)
    )), 500, Some("server screwed up"))
    println(sss(result))

  }

  @Test
  def statistic: Unit = {
//    (ArrayBuffer[(Int, Int, Int, Int, Int, Long)],
//      Array[Int],
//      Array[(String, Int)])
    val t1 = new ArrayBuffer[(Int, Int, Int, Int, Int, Long)](16)
    val t11 = (12, 45, 78, 99, 0, 111111L)
    val t12 = (1, 455, 4378, 99, 40, 222222L)
    val t13 = (1, 55, 78, 99, 45780, 333333L)
    t1 += t11 += t12 += t13

    val t2 = Array(9945, 457, 123, 45, 5)
    val t31 = ("logger1", 99)
    val t32 = ("logger2", 5)
    val t33 = ("logger333", 4599)
    val t3 = Array(t31, t32, t33)


    val tp = (t1, t2, t3)

    val js = Serialization.writePretty(tp.asInstanceOf[AnyRef])(DefaultFormats)
    println(js)
  }


  val LOG: Logger = LoggerFactory.getLogger(classOf[TestWidget])

  def gen(mk: Boolean, except: Boolean) = {

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
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", if (except) exp else null, null)
    if (mk)
      lbEvent.setMarker(mk1)
    val _filter = AppContext.beanAssembler.getBean("classNameFilter").asInstanceOf[StrFilter]

    val fadapt = new LogbackEventAdaptor
    fadapt.from(lbEvent)
  }
}
