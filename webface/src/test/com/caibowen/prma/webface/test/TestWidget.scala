package com.caibowen.prma.webface.test

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.webface.SearchEngine
import com.caibowen.prma.webface.controller.HttpQuery
import org.junit.Test

/**
 * Created by Bowen Cai on 12/26/2014.
 */
class TestWidget {

  @Test
  def stringify(): Unit = {
    val q = new HttpQuery()
    q.maxTime = Long.MaxValue
    q.minTime = Long.MinValue
    q.lowLevel = LogLevel.DEBUG.toString
    q.highLevel = LogLevel.FATAL.toString
    q.exceptionOnly = true
    q.loggerName =
      """
         蔡博文
      """
    val ss = s"$q"
    println(ss)
    q.message =
      """
         "name" is 'msg'
      """
    val s = new SearchEngine().process(q)
    println(s)
  }
}
