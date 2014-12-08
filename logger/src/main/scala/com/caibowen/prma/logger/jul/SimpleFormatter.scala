package com.caibowen.prma.logger.jul

import java.text.MessageFormat
import java.util.logging.{LogRecord, Formatter => JulFormatter}

/**
 * same as java.util.logging.Formatter except that msg formatting is no-blocking
 * @author BowenCai
 * @since  08/12/2014.
 */
class SimpleFormatter extends Formatter {

  @inline
  final def isFormatted(s: String): Boolean = {
    s.indexOf("{0") > 0 || s.indexOf("{1") > 0 || s.indexOf("{2") > 0 || s.indexOf("{3") > 0
  }

  @inline
  override def fmt (record: LogRecord): String ={
    val params = record.getParameters
    val msg = record.getMessage

    if (params != null && params.length > 0 && isFormatted(msg)) {
      MessageFormat.format(msg, params:_*)
    } else
      msg
  }
}
