package com.caibowen.prma.webface

import java.io.IOException
import java.sql.ResultSet

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.{ExceptionVO, EventVO}
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import com.caibowen.prma.query.Q
import com.caibowen.prma.webface.controller.HttpQuery
import com.caibowen.gplume.misc.Str.Utils._
import gplume.scala.context.AppContext
import gplume.scala.jdbc
import gplume.scala.jdbc.{DBSession, DB, SQLOperation}
import org.slf4j.{LoggerFactory, Logger, MDC, MarkerFactory}

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

  //---------------------------------------------------------------------------------------



  def process(q: HttpQuery): List[EventVO] = {

    implicit val b = new StringBuilder(512, """
SELECT id,time_created,level,logger,thread,caller_id,flag,message,reserved FROM `event`
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

    db newSession{implicit session =>

      val eventVOCol = (rs:ResultSet) => {
        val id = rs.getLong(1)
        val res = rs.getObject(8)
        val reserved = if (res == null) None else Some(res.asInstanceOf[Long])
        new EventVO(id, rs.getLong(2), LogLevel.from(rs.getInt(3)),
          rs.getString(4), rs.getString(5), // logger, thread
          Q.callerStackTrace(id),
          rs.getLong(7), // flag
          rs.getString(8), reserved, None, None, None)
      }

      new SQLOperation(b.toString, null).list(eventVOCol)
    }
  }

  def eventDetail(eventID: Long, flag: Long): EventVO =
    db.newSession{implicit session=>
      val tags = if (EventVO.hasTags(flag)) Some(Q.tagsByEventID(eventID)) else None
      val props = if (EventVO.hasProperties(flag)) Some(Q.propsByEventID(eventID)) else None
      val excepts = if (EventVO.hasExceptions(flag)) Some(Q.exceptByEventID(eventID)) else None
      new EventVO(eventID, -1, LogLevel.OFF, "", "", EventVO.NA_ST, flag, "", None, props, excepts, tags)
    }


//  `id` INT NOT NULL,
//  `file` VARCHAR(255) NOT NULL,
//  `class` VARCHAR(255) NULL,
//  `function` VARCHAR(255) NOT NULL,
//  `line` INT NOT NULL,
//  public StackTraceElement(String declaringClass, String methodName,
//    String fileName, int lineNumber) {
  val stackTraceCol = (rs: ResultSet) =>
      new StackTraceElement(rs.getString(3),
                            rs.getString(4),
                            rs.getString(2),
                            rs.getInt(5))

//  `id` BIGINT(20) NOT NULL,
//  `name` VARCHAR(255) NOT NULL,
//  `msg` VARCHAR(255) NULL,

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
