package com.caibowen.prma.logger.logback

import java.lang.StringBuilder

import ch.qos.logback.core.LayoutBase
import com.caibowen.prma.api.EventAdaptor

/**
 * write event to file as JSON ARRAY
 * each line represent one json object,i.e., one EventVO instance
 *
 * Created by Bowen Cai on 1/9/2015.
 */
class JsonLayout[E](adaptor: EventAdaptor[E]) extends LayoutBase[E] {

  require(adaptor != null)

  override def getContentType: String = "application/json"

  super.setFileHeader("{\"prmaLogEntries\":[\r\n")
  super.setPresentationHeader(null)

  override def doLayout(event: E): String = adaptor.from(event).prettyJson(new StringBuilder(512)).toString

  super.setPresentationFooter("\r\n")
  super.setFileFooter("] }")

}
