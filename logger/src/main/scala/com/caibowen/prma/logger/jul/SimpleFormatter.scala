package com.caibowen.prma.logger.jul

import java.text.MessageFormat
import java.util.logging.{LogRecord, Formatter => JulFormatter}

/**
 * same function as java.util.logging.Formatter except that msg formatting is no-blocking
 *
 * @author BowenCai
 * @since  08/12/2014.
 */
class SimpleFormatter extends Formatter {

  @inline
  private final def needFmt(s: String): Boolean = {
    if (s != null && s.length > 3)
      s.indexOf("{0") > 0 || s.indexOf("{1") > 0 || s.indexOf("{2") > 0 || s.indexOf("{3") > 0
    else
      false
  }

  @inline
  override def fmt (record: LogRecord): String ={
    val params = record getParameters
    val msg = record.getMessage // stupid compiler
    if (needFmt(msg)) {
      MessageFormat.format(msg, params:_*)
    } else
      msg
  }
}
