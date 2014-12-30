package com.caibowen.prma.webface

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.webface.controller.HttpQuery
import com.caibowen.gplume.misc.Str.Utils._
import gplume.scala.jdbc.SQLOperation

/**
 * @author BowenCai
 * @since  21/12/2014.
 */
class SearchEngine {

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
