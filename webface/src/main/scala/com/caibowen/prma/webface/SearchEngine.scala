package com.caibowen.prma.webface

import java.io.IOException

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import com.caibowen.prma.webface.controller.HttpQuery
import com.caibowen.gplume.misc.Str.Utils._
import gplume.scala.context.AppContext
import gplume.scala.jdbc.SQLOperation
import org.slf4j.{LoggerFactory, Logger, MDC, MarkerFactory}

/**
 * @author BowenCai
 * @since  21/12/2014.
 */
class SearchEngine {

  val LOG = LoggerFactory.getLogger(classOf[SearchEngine])

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

  def _test: JsonResult[List[EventVO]] =
    new JsonResult[List[EventVO]](List(
      gen(true, false),
      //    gen(false, true),
      gen(false, false)
    ), 500, Some("server screwed up"))


  def process(q: HttpQuery): List[EventVO] = {

    implicit val b = new StringBuilder(512, "SELECT * FROM `event` WHERE "
      + "time_created > ").append(q.minTime)
      .append(" AND time_created < ").append(q.maxTime)
      .append(" AND level > ").append(LogLevel.from(q.lowLevel))
      .append(" AND level < ").append(LogLevel.from(q.highLevel))

    if (notBlank(q.loggerName)) {
      b append " AND logger = "
      quote(q.loggerName)
    }
    if (notBlank(q.threadName)) {
      b append " AND thread = "
      quote(q.threadName)
    }
    // fuzzy q ???
    if (notBlank(q.message)) {
      b append " AND message = "
      quote(q.message)
    }

    if (q.exceptionOnly)
      b append " AND flag > 4294967296"

    b append " ORDER BY time_created DESC LIMIT 4096"

//    val sql = new SQLOperation(b.toString, null)
//    sql.list(rs=>
//    )
    null
  }

  def quote(param: String)(implicit b: StringBuilder): StringBuilder = {
    b append '\''
    for (c <- param) c match {
      case '\n' => b append '\\' append 'n'
      case '\r' => b append '\\' append 'r'
      case '\t' => b append '\\' append 't'
      case '\f' => b append '\\' append 'f'
      case '\\' => b append '\\' append '\\'
      case '\"' => b append '\\' append '\"'
      case '\'' => b append '\\' append '\''
      case '\032' => b append '\\' append 'Z'
      case o => b append o
    }

    b append '\''
  }

}
