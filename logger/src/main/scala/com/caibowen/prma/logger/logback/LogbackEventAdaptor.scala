package com.caibowen.prma.logger.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.{ILoggingEvent, IThrowableProxy}
import com.caibowen.prma.api.model.{EventVO, ExceptionVO}
import com.caibowen.prma.api.{EventAdaptor, LogLevel}

import scala.collection.immutable._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, TreeSet}


/**
 * @author BowenCai
 * @since  02/12/2014.
 */
object LogbackEventAdaptor {

  val NA_ST = new StackTraceElement("?", "?", "?", -1)

  def markers(event: ILoggingEvent): Set[String] = {
    val mk = event.getMarker
    if (mk == null)
      return null
    val mks = new TreeSet[String]
    mks.add(mk.getName)
    val iter = mk.iterator()
    while (iter.hasNext)
      mks.add(iter.next().getName)
    mks.toSet
  }

  def excepts(event: ILoggingEvent): List[ExceptionVO] = {
    val px = event.getThrowableProxy
    if (px == null)
      return null

    val toExceptVO = (px: IThrowableProxy, start: Int) => {
      val stps = px.getStackTraceElementProxyArray
      val buf = new ArrayBuffer[StackTraceElement](16)
      stps.take(stps.length - start).foreach(buf += _.getStackTraceElement)
      new ExceptionVO(px.getClassName, px.getMessage, buf.toList)
    }

    var _cause = px.getCause
    if (_cause == null)
      return List(toExceptVO(px, 0))

    val buf = new ArrayBuffer[ExceptionVO](16)
    buf += toExceptVO(px, 0)
    val cs = _cause.getCommonFrames
    do {
      buf += toExceptVO(_cause, cs)
      _cause = _cause.getCause
    } while (_cause != null)

    buf.toList
  }

  def properties(event: ILoggingEvent): Map[String, String] = {
    import scala.collection.JavaConversions.mapAsScalaMap
    val mmdc = event.getMDCPropertyMap
    val smdc = mmdc.size()
    val mcp = event.getLoggerContextVO.getPropertyMap
    val scp = mcp.size()
    val total = smdc + scp
    if (0 == total)
      return null
    val tb = new mutable.HashMap[String, String]()
    for ((k,v) <- mmdc)
      tb.put(k, v)
    for ((k,v) <- mcp)
      tb.put(k, v)
    tb.toMap
  }
}
class LogbackEventAdaptor extends EventAdaptor[ILoggingEvent]{

  @inline
  private def callerST = (event: ILoggingEvent) => {
    val _callerSTs: Array[StackTraceElement] = event.getCallerData
    var callerST: StackTraceElement = null
    if (_callerSTs != null && _callerSTs.length > 0) callerST = _callerSTs(0)
    else callerST = LogbackEventAdaptor.NA_ST
    callerST
  }

  @inline
  private def logLevel = (event: ILoggingEvent) => {
    val idx = event.getLevel.levelInt / Level.TRACE_INT
    LogLevel.from(idx)
  }


  override def from(event: ILoggingEvent): EventVO = {
    val st = callerST(event)
    val le = logLevel(event)

    new EventVO(event.getTimeStamp, le, event.getLoggerName, event.getThreadName,
        st, event.getFormattedMessage,-1,
      LogbackEventAdaptor.properties(event),
      LogbackEventAdaptor.excepts(event),
      LogbackEventAdaptor.markers(event))
  }

  override def to(vo: EventVO): ILoggingEvent = {
    throw new UnsupportedOperationException
  }

}
