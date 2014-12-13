package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.alibaba.fastjson.JSON
import com.caibowen.gplume.context.AppContext
import com.caibowen.prma.core.ActorBuilder
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import org.junit.Test
import org.slf4j._

import scala.concurrent.duration.Duration

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class LogBackTest extends DBContext {

  val LOG: Logger = LoggerFactory.getLogger(classOf[LogBackTest])
  val adopter = new LogbackEventAdaptor

  @Test
  def adopt: Unit = {
    val exp = new RuntimeException("msg level 3", new IOException("msg level 2", new FileNotFoundException("msg level 1")))

    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")

    val lbEvent = new LoggingEvent("fmt scala logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", exp, null)

    val vo = adopter.from(lbEvent)
    Console.setOut(System.err)
    //    Console.withOut(System.err){}

    println(s"prop ${vo.propertyCount} mk ${vo.markerCount} exp ${vo.exceptionCount} \r\n vo $vo \r\n----\r\n")
    println(JSON.toJSONString(vo.asInstanceOf[AnyRef], true))
    
    eventStore ! vo

  }



}
