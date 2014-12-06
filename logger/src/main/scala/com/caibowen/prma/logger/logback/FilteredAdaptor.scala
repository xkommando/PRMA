package com.caibowen.prma.logger.logback

import ch.qos.logback.classic.spi.{StackTraceElementProxy, IThrowableProxy, ILoggingEvent}
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.filter.StrFilter

import scala.collection.immutable.List
import scala.collection.mutable.ArrayBuffer

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class FilteredAdaptor private[this](val classFilter: StrFilter, val  stackTraceFilter: StrFilter)
  extends LogbackEventAdaptor {

  private def takeClass = (name: String)=>classFilter.accept(name) != 1
  private def takeStackTrace = (sp: StackTraceElementProxy)=>stackTraceFilter.accept(sp.getStackTraceElement.getClassName) != 1

  override def getExcepts(event: ILoggingEvent): List[ExceptionVO] = {

    val px = event.getThrowableProxy
    if (px == null || !takeClass(px.getClassName))
      return null

    val toExceptVO = (px: IThrowableProxy, start: Int) => {
      val stps = px.getStackTraceElementProxyArray
      val sts = stps.take(stps.length - start)
        .filter(takeStackTrace)
        .map(_.getStackTraceElement)
        .toList
      new ExceptionVO(px.getClassName, px.getMessage, sts)
    }

    var _cause = px.getCause
    if (_cause == null || !takeClass(_cause.getClassName))
      return List(toExceptVO(px, 0))

    val buf = new ArrayBuffer[ExceptionVO](16)
    buf += toExceptVO(px, 0)
    val cs = _cause.getCommonFrames
    do {
      buf += toExceptVO(_cause, cs)
      _cause = _cause.getCause
    } while (_cause != null && takeClass(_cause.getClassName))

    buf.toList
  }
}
