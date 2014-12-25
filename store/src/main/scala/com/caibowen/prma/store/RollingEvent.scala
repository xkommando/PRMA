package com.caibowen.prma.store

import com.caibowen.prma.api.LogLevel.LogLevel

/**
 * @author BowenCai
 * @since  16/12/2014.
 */
trait RollingEvent {

  def deleteOldEntry(minTime: Long)

  def deleteLowLevelEntry(lowerBound: LogLevel)

}
