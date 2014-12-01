package com.caibowen.prma.core.filter

import com.caibowen.prma.api.LogLevel

/**
 * @author BowenCai
 * @since  01/12/2014.
 */
class LevelFilter(lower: Int, upper: Int) extends Filter[LogLevel] {

  def this(ls: String, us: String) {
    this(LogLevel.from(ls).levelInt, LogLevel.from(us).levelInt)
  }

  override def doAccept(le: LogLevel): Int = {
    if (lower <= le.levelInt && le.levelInt <= upper)
      1
    else
      -1
  }

}
