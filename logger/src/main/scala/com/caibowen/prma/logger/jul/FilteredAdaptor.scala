package com.caibowen.prma.logger.jul

import java.util.logging.{LogRecord => JulLogRecord}

import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.filter.StrFilter

import scala.collection.immutable.Vector
import scala.collection.mutable.ArrayBuffer

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
class FilteredAdaptor private[this](val classFilter: StrFilter, val  stackTraceFilter: StrFilter)
  extends JulRecordAdaptor {

  private def takeClass = (t: Throwable)
      => classFilter.accept(t.getClass.getName) != 1

  private def takeStackTrace = (sp: StackTraceElement)
      => stackTraceFilter.accept(sp.getClassName) != 1

  override def getExcepts(ev: JulLogRecord): Vector[ExceptionVO] = {

    val _t = ev.getThrown
    if (_t == null || !takeClass(_t))
      return null

    val toVO = (th: Throwable, start: Int) => {
      val stps = th.getStackTrace
      val len = stps.length - start
      val stacks = if (len > 0)
        stps.take(len).filter(takeStackTrace).toVector
      else null.asInstanceOf[Vector[StackTraceElement]]

      new ExceptionVO(th.getClass.getName,
        th.getMessage,
        stacks)
    }

    var cause = _t.getCause
    if (cause == null || !takeClass(cause))
      return Vector(toVO(_t, 0))

    val buf =  Vector.newBuilder[ExceptionVO]
    buf.sizeHint(16)
    buf += toVO(_t, 0)
    val cs = JulRecordAdaptor.commonFrames(_t, cause)
    do {
      buf += toVO(cause, cs)
      cause = cause.getCause
    } while (cause != null && takeClass(cause))
    buf.result()
  }
}
