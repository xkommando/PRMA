package com.caibowen.prma.logger.test

import java.io.IOException

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.core.JSON
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import org.junit.Test
import org.slf4j.{MDC, LoggerFactory, MarkerFactory}

/**
 * @author BowenCai
 * @since  19/12/2014.
 */
class JsonTest {

  val LOG = LoggerFactory.getLogger(classOf[LogBackTest])

  @Test
  def t1(): Unit = {

    val exp = new RuntimeException("msg level 3", new IOException("msg level 2"))//, new FileNotFoundException("msg level 1")))

    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
//    val mdc1: String = "test mdc 1"
//    MDC.put(mdc1, "hahaha")
//    MDC.put("test mdc 2", "hahaha222")
//    MDC.put("test mdc 3", "wowowo")
        MDC.clear()
    val lbEvent = new LoggingEvent("fmt scala logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", exp, null)
//    lbEvent.setMarker(mk1)

    val fadapt = new LogbackEventAdaptor
    val vo = fadapt.from(lbEvent)

    val ex1 = vo.exceptions.get.apply(0)
    val ejs = ex1.toString
    val ex2 = JSON.readExceptionVO(ejs)
    println(ex1 == ex2)

    //
    val jss = vo.toString
    val voo = JSON.readEventVO(jss)
    println(voo.equals(vo))

//    println(jss)
//    lbEvent.setMarker(null)
//    MDC.clear()
    MDC.put("test mdc 3", "wowowo")
    lbEvent.setMarker(mk2)
    println(fadapt.from(lbEvent))

    println(fadapt.from(new LoggingEvent("fmt scala logging store test",
          LOG.asInstanceOf[ch.qos.logback.classic.Logger],
          Level.DEBUG,
          "scala logging store test", null, null)))

  }


}
