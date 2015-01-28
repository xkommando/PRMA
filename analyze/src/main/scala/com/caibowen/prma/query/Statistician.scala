package com.caibowen.prma.query

import com.caibowen.prma.api.LogLevel
import gplume.scala.jdbc.{DBSession, SQLOperation}
import gplume.scala.jdbc.SQLAux._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Bowen Cai on 1/5/2015.
 */
object Statistician {

//  (timeLine, levelCounts, loggerCount, exceptCount)
  def timelineCounter(minTime: Long, maxTime: Long,
                      lowLevel: Int, highLevel: Int,
                      interval: Long = (1000 * 60 * 30).toLong)
                     (implicit session: DBSession):
      (ArrayBuffer[(Int, Int, Int, Int, Int, Long)], // timeline
        Array[Int], // levelCounts
        Array[(String, Array[Int])], // loggerCount \r\n exceptCount
        Array[(String, Int)]) = {

    val raw4 = new SQLOperation("SELECT id, time_created, level, logger_id FROM `event` " +
      " WHERE ? < time_created AND time_created < ? AND ? <= level AND level <= ? " +
      " ORDER BY time_created ASC limit 262144 ").bind(minTime, maxTime, lowLevel, highLevel)
      .array(rs => (rs.getLong(1), rs.getLong(2), rs.getInt(3), rs.getInt(4)))

    if (raw4 == null || raw4.length == 0)
      return null

    //    val levelCounts = raw4.groupBy(_._2).toArray.map(t=>(t._1, t._2.length)

    // 1. count event level
    // Array(...level count...)
    val levelCounts = Array(0, 0, 0, 0, 0)
    // 2. count logger
    val _loggerCount = new mutable.HashMap[Int, Array[Int]]

    val _firstTime = raw4(0)._2
    var _timeCounter = Array(0, 0, 0, 0, 0, _firstTime)
    var _nextTime = _firstTime + interval

    // 3. count time line
    // ArrayBuffer Array (...level count..., time)
    val timeLine = raw4.foldLeft(new ArrayBuffer[(Int, Int, Int, Int, Int, Long)](1024))((ab, t3) => {
      val iLevel = t3._3
      if (iLevel < 9) {
        // 1
        val curTime = t3._2
        if (curTime >= _nextTime) {
          _nextTime = _nextTime + interval
          val now = (_timeCounter(0).toInt, _timeCounter(1).toInt, _timeCounter(2).toInt, _timeCounter(3).toInt, _timeCounter(4).toInt, _timeCounter(5))
          ab += now
          _timeCounter = Array(0, 0, 0, 0, 0, _nextTime)
        }
        val idx = iLevel / 2
        _timeCounter(idx) += 1
        // 2
        levelCounts(idx) += 1


        // 3
        val lgId = t3._4
        //   last one is the overall count
        val update = _loggerCount.getOrElse(lgId, Array(0, 0, 0, 0, 0, 0))
        update(idx) += 1
        update(5) += 1

        _loggerCount.put(lgId, update)
      }

      ab
    })

    // Array ("logger", Array(TRACE, DEBUG, INFO, WARN, ERROR, __ALL_COUNT__))
    val loggerCount = _loggerCount.map(t=>(Q.logggerNameByID(t._1).getOrElse("Undefined"), t._2))
                      .toArray.sortWith(_._2(5) < _._2(5))
                      .take(64)

    val exceptCount = exceptionCount(raw4)

    (timeLine, levelCounts, loggerCount, exceptCount)
  }

  def exceptionCount(raw4: Array[(Long, Long, Int, Int)])(implicit session: DBSession)
  : Array[(String, Int)] = new SQLOperation("""
    SELECT EXP.name as Except_Name, COUNT(EXP.id) AS Except_Count FROM `j_event_exception` AS JEE
    INNER JOIN `exception` AS EXP ON EXP.id = JEE.except_id
    WHERE JEE.event_id IN(?) GROUP BY EXP.id ORDER BY Except_Count DESC  limit 128 """)
    .array(rs=>(rs.getString(1), rs.getInt(2)))

}
