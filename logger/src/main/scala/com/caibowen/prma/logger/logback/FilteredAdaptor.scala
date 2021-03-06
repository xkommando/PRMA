package com.caibowen.prma.logger.logback

import ch.qos.logback.classic.spi.{ILoggingEvent, IThrowableProxy, StackTraceElementProxy}
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.filter.StrFilter

import scala.collection.immutable.{Vector, List}
import scala.collection.mutable.ArrayBuffer

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class FilteredAdaptor(private[this] val classFilter: StrFilter,
                      private[this] val stackTraceFilter: StrFilter)

  extends LogbackEventAdaptor {

  private def takeClass = (name: String)=>classFilter.accept(name) != 1
  private def takeStackTrace = (sp: StackTraceElementProxy)=>stackTraceFilter.accept(sp.getStackTraceElement.getClassName) != 1

  override def getExcepts(event: ILoggingEvent): Vector[ExceptionVO] = {

    val px = event.getThrowableProxy
    if (px == null || !takeClass(px.getClassName))
      return null

    val toExceptVO = (px: IThrowableProxy, start: Int) => {
      val stps = px.getStackTraceElementProxyArray
      val len = stps.length - start
      val stacks = if (len > 0)
        stps.take(stps.length - start)
        .filter(takeStackTrace)
        .map(_.getStackTraceElement)
        .toVector
      else null.asInstanceOf[Vector[StackTraceElement]]
      new ExceptionVO(px.getClassName, px.getMessage,
      stacks)
    }

    var cause = px.getCause
    if (cause == null || !takeClass(cause.getClassName))
      return Vector(toExceptVO(px, 0))

    val buf =  Vector.newBuilder[ExceptionVO]
    buf.sizeHint(16)
    val cs = cause.getCommonFrames
    do {
      buf += toExceptVO(cause, cs)
      cause = cause.getCause
    } while (cause != null && takeClass(cause.getClassName))

    buf.result()
  }
}
