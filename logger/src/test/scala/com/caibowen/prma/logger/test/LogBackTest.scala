package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import java.util.regex.Pattern

import akka.actor.ActorPath
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.gplume.scala.context.AppContext
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.logger.logback.{FilteredAdaptor, LogbackEventAdaptor}
import org.junit.Test
import org.slf4j._

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class LogBackTest extends DBContext {

  val LOG: Logger = LoggerFactory.getLogger(classOf[LogBackTest])
  val adopter = new LogbackEventAdaptor

  def actorName: Unit = {
    val actorNamePattern: Pattern = ActorPath.ElementRegex.pattern
    println(actorNamePattern.matcher("PRMA.Monitor.Actor_No.1").matches())
    println(actorNamePattern.matcher("PRMA.Store.Actor").matches())
  }

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
//    MDC.clear()
    val lbEvent = new LoggingEvent("fmt scala logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala logging store test", exp, null)
    lbEvent.setMarker(mk1)
    val _filter = AppContext.beanAssembler.getBean("classNameFilter").asInstanceOf[StrFilter]

    val fadapt = new FilteredAdaptor(_filter, _filter)
    val vo = fadapt.from(lbEvent)
    Console.setOut(System.err)

    eventStore ! vo
//    val fmt = DefaultFormats + LogLevelSerivializer
//
//    val js = Serialization.writePretty(vo)(fmt)
////    println(js)
////    println("\r\n\r\n")
////    println(vo.toString)
//    println(vo.toString.length)
//    println(vo.exceptions.get.apply(0).toString.length)
//    println(vo.exceptions.get.apply(1).toString.length)
//    val clr = ast \\ "caller"
//    val ls = clr.children
//    ls.foreach(t=>println(t.values))
//    val vvo = Serialization.read(vo.toString)(fmt, ManifestFactory.classType(classOf[EventVO]))
//    println(vvo)
  }


}


//case object StackTraceElemtSerializer[StactTraceElement](
//{
//case JString(s) =>  LogLevel.from(s)
//},
//{
//case d: LogLevel.LogLevel => JString(d.toString)
//}
//))
//)