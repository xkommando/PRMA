package com.caibowen.prma.logger.jul

import java.util.logging.LogRecord

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.LogLevel.LogLevel
import com.caibowen.prma.api.{LogLevel, EventAdaptor}
import com.caibowen.prma.api.model.{ExceptionVO, EventVO}

import scala.collection.mutable.ArrayBuffer

/**
 * @author BowenCai
 * @since  04/12/2014.
 */

class JulRecordAdaptor extends EventAdaptor[LogRecord] {

  override def from(ev: LogRecord): EventVO = {
    val le = JulRecordAdaptor.levelMap(ev.getLevel.intValue())
    val st = new StackTraceElement(ev.getSourceClassName, ev.getSourceMethodName, ev.getSourceClassName, -1)
    return new EventVO(ev.getMillis, le,
      ev.getLoggerName, ev.getThreadID.toString, st,
      ev.getMessage, -1,
      null,
      JulRecordAdaptor.toExcepts(ev),
      null)
  }
  override def to(vo: EventVO): LogRecord = throw new NotImplementedError()
}
object JulRecordAdaptor {

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


  // static int findNumberOfCommonFrames(StackTraceElement[] steArray,
  //     StackTraceElementProxy[] parentSTEPArray) {
  //   if (parentSTEPArray == null || steArray == null) {
  //     return 0;
  //   }

  //   int steIndex = steArray.length - 1;
  //   int parentIndex = parentSTEPArray.length - 1;
  //   int count = 0;
  //   while (steIndex >= 0 && parentIndex >= 0) {
  //     StackTraceElement ste = steArray[steIndex];
  //     StackTraceElement otherSte = parentSTEPArray[parentIndex].ste;
  //     if (ste.equals(otherSte)) {
  //       count++;
  //     } else {
  //       break;
  //     }
  //     steIndex--;
  //     parentIndex--;
  //   }
  //   return count;
  // }

  @inline
  def commonFrames(t1: Throwable, t2: Throwable): Int ={
    throw new NotImplementedError()
  }

  @inline
  def toVO(th: Throwable, start: Int) = {
    val stps = th.getStackTrace
    new ExceptionVO(th.getClass.getName,
      th.getMessage,
      stps.take(stps.length - start).toList)
  }

  def toExcepts(ev: LogRecord): List[ExceptionVO] = {
    val _t = ev.getThrown
    if (_t == null)
      return null

    var cause = _t.getCause
    if (cause == null)
      return List(toVO(_t, 0))

    val buf = new ArrayBuffer[ExceptionVO](16)
    buf += toVO(_t, 0)
    val cs = commonFrames(_t, cause)
    do {
      buf += toVO(cause, cs)
      cause = cause.getCause
    } while (cause != null)
    buf.toList
  }

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