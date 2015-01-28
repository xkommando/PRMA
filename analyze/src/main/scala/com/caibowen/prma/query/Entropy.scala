package com.caibowen.prma.query

import java.sql.ResultSet

import gplume.scala.jdbc.{SQLOperation, DBSession}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Bowen Cai on 1/14/2015.
 */
object Entropy {
  //                id   time reserved(IP), thread, logger, caller, msg
  type LogEntry = (Long, Long, Long, Int, Int, Int, String)
  type LogArray = Array[LogEntry]

//  ipE, threadE, loggerE, callerE, timeE
  type Entropy5 = (Double, Double, Double, Double, Double)

  val SELECT =
    """SELECT EV.id, EV.time_created, EV.reserved, EV.thread_id, EV.logger_id, EV.caller_id, EV.msg,
      FROM `event` as EV """
  val col = (rs:ResultSet) => (rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getInt(4), rs.getInt(5), rs.getInt(6),  rs.getString(7))

  def t(minTime: Long, maxTime: Long)
       (implicit session: DBSession): Entropy5 = {

    val raw = new SQLOperation(SELECT
      + " WHERE 4294967295 < EV.flag  AND ? < EV.time_created AND EV.time_created < ? limit 262144 ")
      .bind(minTime, maxTime)
      .array(col)

    entropy5(raw)
  }


  /**
   * IP, logger, thread, caller, time
   * @param arr
   * @return ipE, threadE, loggerE, callerE, timeE
   *
   * time precision: 3 second
   */
  def entropy5(arr: LogArray): Entropy5 = {

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
    arr.foreach(va=>{
      val ip = va._3
      ipGrp.put(ip, ipGrp.getOrElse(ip, 0) + 1)

      val loggerId = va._5
      loggerGrp.put(loggerId, loggerGrp.getOrElse(loggerId, 0) + 1)

      val threadId = va._4
      threadGrp.put(threadId, threadGrp.getOrElse(threadId, 0) + 1)

      val callerId = va._6
      callerGrp.put(callerId, callerGrp.getOrElse(callerId, 0) + 1)

      // time precision: 3 second
      val time = (va._2 / 3000L).toInt
      timeGrp.put(time, timeGrp.getOrElse(time, 0) + 1)
    })

    val count = arr.size
    val ipE = entropy(count, ipGrp.valuesIterator)
    val callerE = entropy(count, callerGrp.valuesIterator)
    val threadE = entropy(count, threadGrp.valuesIterator)
    val loggerE = entropy(count, loggerGrp.valuesIterator)
    val timeE = entropy(timeGrp.size, timeGrp.valuesIterator)

    (ipE, threadE, loggerE, callerE, timeE)
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
