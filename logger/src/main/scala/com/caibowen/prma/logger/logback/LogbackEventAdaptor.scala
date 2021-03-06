package com.caibowen.prma.logger.logback

import java.net.InetAddress

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.{ILoggingEvent, IThrowableProxy}
import com.caibowen.gplume.misc.Str
import com.caibowen.prma.api.model.{EventVO, ExceptionVO}
import com.caibowen.prma.api.{EventAdaptor, LogLevel}

import scala.collection.JavaConversions._
import scala.collection.immutable._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, TreeSet}


/**
 * @author BowenCai
 * @since  02/12/2014.
 */
class LogbackEventAdaptor extends EventAdaptor[ILoggingEvent]{

  override def from(event: ILoggingEvent): EventVO = {
    val st = LogbackEventAdaptor.getCallerST(event)
    val le = LogbackEventAdaptor.logLevel(event)

    new EventVO(event.getTimeStamp, le, event.getLoggerName, event.getThreadName,
        st, event.getFormattedMessage, LogbackEventAdaptor.localIP,
      getProperties(event),
      getExcepts(event),
      getMarkers(event))
  }

  override def to(vo: EventVO): ILoggingEvent = ???


  def getExcepts(event: ILoggingEvent): Vector[ExceptionVO] = {
    val px = event.getThrowableProxy
    if (px == null)
      return null

    val toExceptVO = (px: IThrowableProxy, start: Int) => {
      val stps = px.getStackTraceElementProxyArray
      val len = stps.length - start
      val stacks = if (len > 0) {
        val buf = Vector.newBuilder[StackTraceElement]
        buf.sizeHint(32)
        stps.take(stps.length - start).foreach(buf += _.getStackTraceElement)
        buf.result()
      } else null.asInstanceOf[Vector[StackTraceElement]]
      new ExceptionVO(px.getClassName, px.getMessage, stacks)
    }

    var _cause = px.getCause
    if (_cause == null)
      return Vector(toExceptVO(px, 0))

    val buf =  Vector.newBuilder[ExceptionVO]
    buf.sizeHint(16)
    buf += toExceptVO(px, 0)
    val cs = _cause.getCommonFrames
    do {
      buf += toExceptVO(_cause, cs)
      _cause = _cause.getCause
    } while (_cause != null)

    buf.result()
  }

  def getMarkers(event: ILoggingEvent): Set[String] = {
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

  def getProperties(event: ILoggingEvent): Map[String, String] = {
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

object LogbackEventAdaptor {


  val localIP = Str.Utils.ipV4ToLong(InetAddress.getLocalHost.getHostAddress)

  @inline
  private def getCallerST = (event: ILoggingEvent) => {
    val _callerSTs: Array[StackTraceElement] = event.getCallerData
    var callerST: StackTraceElement = null
    if (_callerSTs != null && _callerSTs.length > 0) callerST = _callerSTs(0)
    else callerST = EventVO.NA_ST
    callerST
  }

  @inline
  private def logLevel = (event: ILoggingEvent) => {
    val idx = event.getLevel.levelInt / Level.TRACE_INT
    LogLevel.from(idx)
  }
}
