package com.caibowen.prma.logger.jul

import java.net.InetAddress
import java.util.logging.{LogRecord => JulLogRecord}
import javax.annotation.{Nonnull, Nullable}

import com.caibowen.gplume.misc.Str
import com.caibowen.prma.api.LogLevel.LogLevel
import com.caibowen.prma.api.model.{EventVO, ExceptionVO}
import com.caibowen.prma.api.{EventAdaptor, LogLevel}
import com.caibowen.prma.logger.logback.LogbackEventAdaptor

import scala.collection.mutable.ArrayBuffer

/**
 * @author BowenCai
 * @since  04/12/2014.
 */

class JulRecordAdaptor(private[this] val formatter: Formatter = new SimpleFormatter) extends EventAdaptor[JulLogRecord] {

  override def from(ev: JulLogRecord): EventVO = {
    val le = JulRecordAdaptor.levelMap(ev.getLevel.intValue())

    val st = JulRecordAdaptor.getCallerST(ev)
    val msg = formatter.fmt(ev)
    val loggerName = if (ev.getLoggerName == null) "" else ev.getLoggerName
    return new EventVO(ev.getMillis, le,
      loggerName, ev.getThreadID.toString, st,
      msg,
      JulRecordAdaptor.localIP,
      null,
      getExcepts(ev),
      null)
  }
  override def to(vo: EventVO): JulLogRecord = ???

  @Nullable
  def getExcepts(ev: JulLogRecord): List[ExceptionVO] = {

    @inline
    val toVO = (th: Throwable, start: Int) => {
      val stps = th.getStackTrace
      new ExceptionVO(th.getClass.getName,
        th.getMessage,
        stps.take(stps.length - start).toList)
    }

    val _t = ev.getThrown
    if (_t == null)
      return null

    var cause = _t.getCause
    if (cause == null)
      return List(toVO(_t, 0))

    val buf = new ArrayBuffer[ExceptionVO](16)
    buf += toVO(_t, 0)
    val cs = JulRecordAdaptor.commonFrames(_t, cause)
    do {
      buf += toVO(cause, cs)
      cause = cause.getCause
    } while (cause != null)
    buf.toList
  }

}
object JulRecordAdaptor {

  val localIP = Str.Utils.ipV4ToLong(InetAddress.getLocalHost.getHostAddress)

  @Nonnull
  def getCallerST(record: JulLogRecord): StackTraceElement = {
    if (record.getSourceClassName != null)
      new StackTraceElement(record.getSourceClassName, record.getSourceMethodName, record.getSourceClassName, -1)
    else {
      val sts = new Throwable().getStackTrace
      if (sts != null && sts.length > 2)
        sts(2)
      else LogbackEventAdaptor.NA_ST
    }

  }

  @inline
  def commonFrames(t1: Throwable, t2: Throwable): Int ={
    val s1 = t1.getStackTrace
    val s2 = t2.getStackTrace
    if (s1 == null || s2 == null)
      0
    else {
      var count = 0
      for ((i, j) <- (s1 zip s2).reverse if i.equals(j))
        count += 1
      count
    }
  }

  private[jul] val levelMap = Map[Int, LogLevel](
    Int.MinValue -> LogLevel.ALL,
    300 -> LogLevel.TRACE,
    400 -> LogLevel.DEBUG,
    500 -> LogLevel.DEBUG,
    700 -> LogLevel.INFO,
    800 -> LogLevel.INFO,
    900 -> LogLevel.WARN,
    1000 -> LogLevel.ERROR,
    Int.MaxValue -> LogLevel.OFF)
}
/*
 OFF = new Level("OFF",Integer.MAX_VALUE, defaultBundle); OFF 32

   SEVERE = new Level("SEVERE",1000, defaultBundle); ERROR 16

   WARNING = new Level("WARNING", 900, defaultBundle); WARN 8

   INFO = new Level("INFO", 800, defaultBundle);INFO
   CONFIG = new Level("CONFIG", 700, defaultBundle);INFO 4

   FINE = new Level("FINE", 500, defaultBundle);DEBUG
   FINER = new Level("FINER", 400, defaultBundle);DEBUG 2


   FINEST = new Level("FINEST", 300, defaultBundle);TRACE 1

   ALL = new Level("ALL", Integer.MIN_VALUE, defaultBundle);ALL 0
 */