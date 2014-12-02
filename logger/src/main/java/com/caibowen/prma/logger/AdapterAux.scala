package com.caibowen.prma.logger

import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
object AdapterAux {

  val NA_ST = new StackTraceElement("?", "?", "?", -1)

  def callerST (event: ILoggingEvent): StackTraceElement = {
    val _callerSTs: Array[StackTraceElement] = event.getCallerData
    var callerST: StackTraceElement = null
    if (_callerSTs != null && _callerSTs.length > 0) callerST = _callerSTs(0)
    else callerST = NA_ST
    callerST
  }
}
