package com.caibowen.prma.webface

import java.sql.ResultSet

import com.caibowen.gplume.misc.Str.Utils.notBlank
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.query.Q
import com.caibowen.prma.webface.controller.HttpQuery
import gplume.scala.jdbc.{DB, SQLOperation}
import gplume.scala.jdbc.SQLAux.quoteTo
/**
 * @author BowenCai
 * @since  21/12/2014.
 */
class SearchEngine(db: DB) {


  def process(q: HttpQuery): Seq[EventVO] = {

    implicit val b = new StringBuilder(512)
      .append(" time_created > ").append(q.minTime)
      .append(" AND time_created < ").append(q.maxTime)
      .append(" AND level > ").append(LogLevel.from(q.lowLevel))
      .append(" AND level < ").append(LogLevel.from(q.highLevel))

    if (notBlank(q.loggerName)) {
      b append " AND logger = "
      quoteTo(q.loggerName)
    }
    if (notBlank(q.threadName)) {
      b append " AND thread = "
      quoteTo(q.threadName)
    }
    // fuzzy q ???
    if (notBlank(q.message)) {
      b append " AND message = "
      quoteTo(q.message)
    }

    if (q.exceptionOnly)
      b append " AND flag > 4294967296"

    b append " ORDER BY time_created DESC LIMIT 4096"

    listSimple(b.toString())
  }


  val eventVOCol = (rs:ResultSet) => {

    val loggerID = rs.getInt(4).toString
    val threadID = rs.getInt(5).toString
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
      loggerID, threadID, // logger, thread
      stackTrace,
      rs.getLong(6), // flag
      rs.getString(7), reserved, None, None, None)
  }

  /**
   * @param filterStr
   * @return a simple list of eventVO, i.e., no exceptions, tags or properties
   */
  private def listSimple(filterStr: String): Seq[EventVO] = {

    implicit val b = new StringBuilder(512,
      """ SELECT EV.id,EV.time_created,EV.level,EV.logger_id,EV.thread_id,EV.flag,EV.message,EV.reserved,
    SK.file,SK.class,SK.function,SK.line
   FROM `event` AS EV
   INNER JOIN `stack_trace` AS SK ON SK.id = EV.caller_id
WHERE """)
    .append(filterStr)

    db readOnlySession{implicit session =>
      new SQLOperation(b.toString, null).array(eventVOCol)
    }
  }

  def detailedEvent(eventID: Long, threadID: Int, loggerID: Int, flag: Long): EventVO =
    db.readOnlySession{implicit session=>
      val loggerName = Q.logggerNameByID(loggerID, "Undefined")
      val threadName = Q.threadNameByID(threadID, "Undefined")
//      val threadName = Q.threadNameByID()
      val tags = if (EventVO.hasTags(flag)) Some(Q.tagsByEventID(eventID)) else None
      val props = if (EventVO.hasProperties(flag)) Some(Q.propsByEventID(eventID)) else None
      val excepts = if (EventVO.hasExceptions(flag)) Some(Q.exceptByEventID(eventID)) else None
      new EventVO(eventID, -1, LogLevel.OFF,
                loggerName, threadName, EventVO.NA_ST, flag, "", None, props, excepts, tags)
    }

}
