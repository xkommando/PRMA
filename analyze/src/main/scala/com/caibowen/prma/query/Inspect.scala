package com.caibowen.prma.query

import java.sql.ResultSet

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import gplume.scala.jdbc.{SQLOperation, DBSession}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Bowen Cai on 1/14/2015.
 */
object Inspect {

//  ipE, threadE, loggerE, callerE, timeE
  type Entropy5 = Array[Double]

  val SELECT =
//    EV.id,EV.time_created,EV.level,EV.logger_id,EV.thread_id,EV.flag,EV.message,EV.reserved,
  //            1          2                3          4               5        6        7            8           9
  //     10        11         12     13
  //     14          15
    """SELECT EV.id, EV.time_created, EV.level, EV.logger_id, EV.thread_id, EV.flag, EV.message, EV.reserved, EV.caller_id,
      SK.file,SK.class,SK.function, SK.line,
      LG.value, THD.value,
      FROM `event` AS EV
      INNER JOIN `stack_trace` AS SK ON SK.id = EV.caller_id
      INNER JOIN `thread` AS THD ON THD.id = EV.thread_id
      INNER JOIN `logger` AS LG ON LG.id = EV.logger_id
      """
  val col = (rs:ResultSet) => {
    val _line = rs.getInt(13)
    val stackTrace = if (_line <= 0)
      EventVO.NA_ST
    else
      new StackTraceElement(rs.getString(11),//class
        rs.getString(12), // method
        rs.getString(10), // file
        _line)

    val id = rs.getLong(1)
    val loggerID = rs.getInt(4)
    val loggerName = rs.getString(14)
    val threadID = rs.getInt(5)
    val threadName = rs.getString(15)

    new EventVO(id, rs.getLong(2), LogLevel.from(rs.getInt(3)),
      loggerName, threadName, // logger, thread
      stackTrace,
      rs.getLong(6), rs.getString(7), // flag msg
      Some(rs.getLong(8)), None, None, None)
  }



  def timePeriod(minTime: Long, maxTime: Long, lowLevel: Int, highLevel: Int)
       (implicit session: DBSession): (Array[EventVO], Entropy5) = {

    val ls = new SQLOperation(SELECT
      + " WHERE 4294967295 < EV.flag  AND ? < EV.time_created AND EV.time_created < ? limit 262144 ")
      .bind(minTime, maxTime)
      .array(col)

    (ls, entropy5(ls))
  }

  def exception(name: String)(implicit session: DBSession): (Array[EventVO], Entropy5) = {

    val ls = new SQLOperation(SELECT +
  """ INNER JOIN j_event_exception AS JEE ON JEE.event_id = EV.id
      INNER JOIN exception AS EXP AS EXP.id = JEE.except_id
      WHERE EXP.name = ?
  """).bind(name).array(col) ////////////////////////////// SEQ

    (ls, entropy5(ls))
  }

  def customQuery(sql: String)(implicit session: DBSession): (Array[EventVO], Entropy5) = {
    val ls = new SQLOperation(SELECT + sql).array(col)
    (ls, entropy5(ls))
  }



  /**
   * IP, logger, thread, caller, time
   * @param arr
   * @return ipE, threadE, loggerE, callerE, timeE
   *
   * time precision: 3 second
   */
  def entropy5(arr: Array[EventVO]): Entropy5 = {

    val ipGrp = new mutable.HashMap[Long, Int]
    ipGrp.sizeHint(128)
    val loggerGrp = new mutable.HashMap[Int, Int]
    loggerGrp.sizeHint(128)
    val threadGrp = new mutable.HashMap[Int, Int]
    threadGrp.sizeHint(128)
    val callerGrp = new mutable.HashMap[Int, Int]
    callerGrp.sizeHint(512)
    val timeGrp = new mutable.HashMap[Int, Int]
    timeGrp.sizeHint(1024)

    //                id   time reserved(IP), thread, logger, caller, msg
//    type LogEntry = (Long, Long, Long, Int, Int, Int, String)
    arr.foreach(vo=> {

      val ip = vo.reserved.get
      ipGrp.put(ip, ipGrp.getOrElse(ip, 0) + 1)

      val loggerId = vo.loggerName.hashCode
      loggerGrp.put(loggerId, loggerGrp.getOrElse(loggerId, 0) + 1)

      val threadId = vo.threadName.hashCode
      threadGrp.put(threadId, threadGrp.getOrElse(threadId, 0) + 1)

      val callerId = vo.callerStackTrace.hashCode()
      callerGrp.put(callerId, callerGrp.getOrElse(callerId, 0) + 1)

      // time precision: 3 second
      val time = (vo.timeCreated / 3000L).toInt
      timeGrp.put(time, timeGrp.getOrElse(time, 0) + 1)
    })

    val count = arr.size
    val ipE = entropy(count, ipGrp.valuesIterator)
    val callerE = entropy(count, callerGrp.valuesIterator)
    val threadE = entropy(count, threadGrp.valuesIterator)
    val loggerE = entropy(count, loggerGrp.valuesIterator)

    val timeE = entropy(timeGrp.size, timeGrp.valuesIterator)

    Array(ipE, threadE, loggerE, callerE, timeE)
  }

  @inline
  def entropy(size: Int, iter: Iterator[Int]): Double =
    - iter.foldLeft(0.0)((sum, count) => {
      val p = count.toDouble / size
      sum + log2(p) * p
    })

  def exceptionCount(): Unit ={

  }

  import StrictMath.{log10, exp}

  def ln = StrictMath.log(_)
  val log2baseE = StrictMath.log(2.0)
  val log3baseE = StrictMath.log(3.0)
  val log5baseE = StrictMath.log(5.0)
  val log6baseE = StrictMath.log(6.0)
  val log7baseE = StrictMath.log(7.0)
  val log9baseE = StrictMath.log(9.0)
  def log2 = StrictMath.log(_: Double) / log2baseE
  def log3 = StrictMath.log(_: Double) / log3baseE
  def log5 = StrictMath.log(_: Double) / log5baseE
  def log6 = StrictMath.log(_: Double) / log6baseE
  def log7 = StrictMath.log(_: Double) / log7baseE
  def log9 = StrictMath.log(_: Double) / log9baseE
  def log(base: Double, v: Double) = ln(v) / ln(base)

}
