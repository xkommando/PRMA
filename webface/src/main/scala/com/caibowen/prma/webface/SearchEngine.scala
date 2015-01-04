package com.caibowen.prma.webface

import java.io.IOException
import java.sql.ResultSet

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.gplume.context.AppContext
import com.caibowen.gplume.misc.Str.Utils._
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import com.caibowen.prma.query.Q
import com.caibowen.prma.webface.controller.HttpQuery
import gplume.scala.jdbc.{DB, SQLOperation}
import org.slf4j.{MDC, MarkerFactory, LoggerFactory}

/**
 * @author BowenCai
 * @since  21/12/2014.
 */
class SearchEngine(db: DB) {

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
      val fadapt = new LogbackEventAdaptor
      fadapt.from(lbEvent)
    }

    def _test  =
      List(
        gen(true, false),
        //    gen(false, true),
        gen(false, false)
      )

  val eventVOCol = (rs:ResultSet) => {
    val _res = rs.getObject(8)
    val reserved = if (_res == null) None else Some(_res.asInstanceOf[Long])

    val _line = rs.getInt(12)
    val stackTrace = if (_line <= 0)
      EventVO.NA_ST
    else new StackTraceElement(rs.getString(10),
        rs.getString(11),
        rs.getString(9),
        _line)

    new EventVO(rs.getLong(1), rs.getLong(2), LogLevel.from(rs.getInt(3)),
      rs.getString(4), rs.getString(5), // logger, thread
      stackTrace,
      rs.getLong(6), // flag
      rs.getString(7), reserved, None, None, None)
  }

  def process(q: HttpQuery): List[EventVO] = {

    implicit val b = new StringBuilder(512,
""" SELECT EV.id,EV.time_created,EV.level,EV.logger,EV.thread,EV.flag,EV.message,EV.reserved,
    SK.file,SK.class,SK.function,SK.line
   FROM `event` AS EV
   INNER JOIN `stack_trace` AS SK ON SK.id = EV.caller_id
WHERE time_created > """).append(q.minTime)
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

    db readOnlySession{implicit session =>
      new SQLOperation(b.toString, null).list(eventVOCol)
    }
  }

  def eventDetail(eventID: Long, flag: Long): EventVO =
    db.readOnlySession{implicit session=>
      val tags = if (EventVO.hasTags(flag)) Some(Q.tagsByEventID(eventID)) else None
      val props = if (EventVO.hasProperties(flag)) Some(Q.propsByEventID(eventID)) else None
      val excepts = if (EventVO.hasExceptions(flag)) Some(Q.exceptByEventID(eventID)) else None
      new EventVO(eventID, -1, LogLevel.OFF, "", "", EventVO.NA_ST, flag, "", None, props, excepts, tags)
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
