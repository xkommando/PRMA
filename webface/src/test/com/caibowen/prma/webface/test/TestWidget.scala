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
import org.junit.Test
import org.slf4j.{LoggerFactory, Logger, MDC, MarkerFactory}

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
    val ss = s"$q"
    println(ss)
    q.message =
      """
         "name" is 'msg'
      """
    val s = new SearchEngine().process(q)
    println(s)
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
  @Test
  def testResolve: Unit ={
    val resolver = new JsonViewResolver
    val result = new JsonResult[List[EventVO]](List(
    gen(true, false),
//    gen(false, true),
    gen(false, false)
    ), 500, Some("server screwed up"))
    println(sss(result))

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
