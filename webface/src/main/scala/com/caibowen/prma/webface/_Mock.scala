package com.caibowen.prma.webface

import java.io.IOException
import java.sql.ResultSet

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import org.slf4j.{MDC, MarkerFactory, LoggerFactory}

/**
 * Created by Bowen Cai on 1/5/2015.
 */
class _Mock {
  val LOG = LoggerFactory.getLogger(classOf[SearchEngine])

  def gen(mk: Boolean, except: Boolean) = {

    val exp = new RuntimeException("msg level 3", new IOException("msg level 2"))//, new FileNotFoundException("msg level 1")))

    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")
    //    MDC.clear()
    val lbEvent = new LoggingEvent("fmt scala logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", if (except) exp else null, null)
    if (mk)
      lbEvent.setMarker(mk1)
    val fadapt = new LogbackEventAdaptor
    fadapt.from(lbEvent)
  }

  def _test  =
    List(
      gen(true, false),
      //    gen(false, true),
      gen(false, false)
    )

}
