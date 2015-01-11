package com.caibowen.prma.logger.jul


import java.util.logging.{LogRecord, Formatter, Handler}
import java.lang.StringBuilder
import com.caibowen.prma.api.EventAdaptor

/**
 * Created by Bowen Cai on 1/9/2015.
 */
class JsonFormatter(adaptor: EventAdaptor[LogRecord]) extends Formatter {

  override def getHead(h: Handler) = "{\"prmaLogEntries\":[\r\n"

  override def format (record: LogRecord): String
  = adaptor.from(record).appendJson(new StringBuilder(512)).append("\r\n").toString

  override def getTail (h: Handler) = "] }"
}
