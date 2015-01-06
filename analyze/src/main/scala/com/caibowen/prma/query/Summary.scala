package com.caibowen.prma.query

import com.caibowen.prma.api.LogLevel
import gplume.scala.jdbc.{DBSession, SQLOperation}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Bowen Cai on 1/5/2015.
 */
class Summary(interval: Long = (1000 * 60 * 30).toLong) {

  def ssss(minTime: Long, maxTime: Long)(implicit session: DBSession): Unit = {
    val rawLs = new SQLOperation("SELECT time_created, level FROM `event` " +
      " WHERE ? < time_created AND time_created < ? " +
      " ORDER BY time_created ASC", null).array(rs=>(rs.getLong(1), rs.getInt(2)))
    if (rawLs == null)
      return

    val _firstTime = rawLs(0)._1
    var counter = Array(0, 0, 0, 0, 0, _firstTime)
    var nextTime = _firstTime + interval
    rawLs.foldLeft(new ArrayBuffer[(Int, Int, Int, Int, Int, Long)](48))((ab, t2) => {
      val iLevel = t2._2
      if (iLevel < 9) {
        val curTime = t2._1
        if (curTime >= nextTime) {
          nextTime = nextTime + interval
          val now = (counter(0).toInt, counter(1).toInt, counter(2).toInt, counter(3).toInt, counter(4).toInt, counter(5))
          ab += now
          counter = Array(0, 0, 0, 0, 0, nextTime)
        }
        counter(iLevel / 2) += 1
      }
      ab
    })



  }
}
