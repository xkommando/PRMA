package com.caibowen.prma.logger.jul

import java.util.logging.LogRecord

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
trait Formatter {
  def fmt(record: LogRecord): String
}
