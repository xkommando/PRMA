package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import java.util.regex.Pattern

import akka.actor.ActorPath
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.gplume.scala.context.AppContext
import com.caibowen.prma.api.model.{EventVO, ExceptionVO}
import com.caibowen.prma.core.filter.StrFilter
import com.caibowen.prma.core.{EventVOSerializer, LogLevelSerializer, StackTraceSerializer}
import com.caibowen.prma.logger.logback.{FilteredAdaptor, LogbackEventAdaptor}
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.{DefaultFormats, TypeInfo, parse}
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

//    eventStore ! vo
    val fmt = DefaultFormats + LogLevelSerializer + StackTraceSerializer
    val js = vo.exceptions.get.apply(0).toString
    val ast = parse(js)
    val data = ast.extract[ExceptionVO](fmt, Manifest.classType(ExceptionVO.getClass))
    println(data)

//    val js = Serialization.writePretty(vo)(fmt)
//
    val jss = vo.toString
    val ti = TypeInfo(classOf[EventVO], None)
    val voo = EventVOSerializer.deserialize(DefaultFormats)(ti, JString(jss))
    println(voo)
//    println(voo.equals(vo))

//    println(js)
//    val ast = parse(jss)
//    println(ast.children)
//    println(ast.children(1))
//    println(ast \ "id").asInstanceOf[JInt]
//    println((ast \ "exceptions").children)

//    println((ast \ "exceptions"))
//    println("----------------------")
//    println(ast \ "callerStackTrace")
//    net.liftweb.json.Extraction.extract()
//    val mani = Manifest.classType[EventVO](EventVO.getClass)
//    val data = ast.extract[EventVO](fmt, mani)
//    println(data.toString)

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