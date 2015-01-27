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


  def timelineCounter(minTime: Long, maxTime: Long,
                      lowLevel: Int, highLevel: Int,
                      interval: Long = (1000 * 60 * 30).toLong)
                     (implicit session: DBSession):
      (ArrayBuffer[(Int, Int, Int, Int, Int, Long)],
        Array[Int],
        Array[(String, Int)]) = {

    val raw3 = new SQLOperation("SELECT time_created, level, logger_id FROM `event` " +
      " WHERE ? < time_created AND time_created < ? AND ? <= level AND level <= ? " +
      " ORDER BY time_created ASC").bind(minTime, maxTime, lowLevel, highLevel)
      .array(rs => (rs.getLong(1), rs.getInt(2), rs.getInt(3)))

    if (raw3 == null || raw3.length == 0)
      return null

    //    val levelCounts = raw3.groupBy(_._2).toArray.map(t=>(t._1, t._2.length)

    // 1. count event level
    // Array(...level count...)
    val levelCounts = Array(0, 0, 0, 0, 0)
    // 2. count logger
    val _loggerCount = new mutable.HashMap[Int, Int]

    val _firstTime = raw3(0)._1
    var _timeCounter = Array(0, 0, 0, 0, 0, _firstTime)
    var _nextTime = _firstTime + interval

    // 3. count time line
    // ArrayBuffer Array (...level count..., time)
    val timeLine = raw3.foldLeft(new ArrayBuffer[(Int, Int, Int, Int, Int, Long)](1024))((ab, t3) => {
      val iLevel = t3._2
      if (iLevel < 9) {
        // 1
        val curTime = t3._1
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
      }
      // 3
      val lgId = t3._3
      _loggerCount.put(lgId, _loggerCount.getOrElse(lgId, 0) + 1)

      ab
    })

    // Array ("logger", count)
    val loggerCount = _loggerCount.map(t=>(Q.logggerNameByID(t._1).getOrElse("Undefined"), t._2))
                      .toArray.sortWith(_._2 < _._2)
                      .take(36)

    (timeLine, levelCounts, loggerCount)
  }

}
