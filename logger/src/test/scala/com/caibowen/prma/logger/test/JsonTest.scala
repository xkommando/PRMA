package com.caibowen.prma.logger.test

import java.io.IOException
import java.lang.StringBuilder

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.caibowen.prma.api.model.Helper
import com.caibowen.prma.core.JSON
import com.caibowen.prma.logger.logback.LogbackEventAdaptor
import org.junit.Test
import org.slf4j.{MDC, LoggerFactory, MarkerFactory}
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.{parse, _}
import net.liftweb.json._

/**
 * @author BowenCai
 * @since  19/12/2014.
 */
class JsonTest {

  val LOG = LoggerFactory.getLogger(classOf[LogBackTest])

  @Test
  def t1(): Unit = {

    val exp = new RuntimeException("msg level 3 异\r\n", new IOException("msg level 2 \"常\" \b\t"))//, new FileNotFoundException("msg level 1")))

    val mk1 = MarkerFactory.getMarker("marker 1")
    val mk2 = MarkerFactory.getMarker("marker 2")
    mk1.add(mk2)
    val mdc1: String = "test mdc 1"
    MDC.put(mdc1, "hahaha")
    MDC.put("test mdc 2", "hahaha222")
    MDC.put("test mdc 3", "wowowo")
//        MDC.clear()
    val lbEvent = new LoggingEvent("fmt logging store test",
      LOG.asInstanceOf[ch.qos.logback.classic.Logger],
      Level.DEBUG,
      "scala 蔡博文 哈哈哈哈 \r\n \t\t\b logging store test", null, null)
    lbEvent.setMarker(mk1)

    val fadapt = new LogbackEventAdaptor
    val vo = fadapt.from(lbEvent)

//    val ex1 = vo.exceptions.get.apply(0)
//    val ejs = ex1.toString
//    println(ejs)
//    val ex2 = JSON.readExceptionVO(ejs)
//    println(ex1 == ex2)

    //
    val jss = vo.appendJson(new StringBuilder(1024)).toString
    println(jss)
    val voo = JSON.readEventVO(jss)
    println(voo.equals(vo))

//    val es1 = vo.exceptions.get
//    val es2 = voo.exceptions.get
//    val e = es1.equals(es2)
//    println(e)
//
//    printf(vo.appendJson(new java.lang.StringBuilder(1024)).toString)
//    lbEvent.setMarker(null)
//    MDC.clear()
//    MDC.put("test mdc 3", "wowowo")
//    lbEvent.setMarker(mk2)
//    println(fadapt.from(lbEvent))

//    println(fadapt.from(new LoggingEvent("fmt scala logging store test",
//          LOG.asInstanceOf[ch.qos.logback.classic.Logger],
//          Level.DEBUG,
//          "scala logging store test", null, null)))

  }

  val cpx = """
        asfsdf
        asfs
        sdf
                蔡博文 haha
                哈哈
            """

  case class XJ(s:String = cpx,
           i: Int = 5, b: Byte = 99.toByte, f: Double = 5.55, map: Map[String, String] = Map("key"->"""
        asfsdf
        asfs
        sdf
                蔡博文 haha
                哈哈
                                                                                                """))

  @Test
  def t2: Unit = {
    import net.liftweb.json.JsonAST._
    import net.liftweb.json.Extraction._
    import net.liftweb.json.Printer._

    implicit val fmt = DefaultFormats //+ ShortTypeHints(List (classOf[ XJ]))
    val js2 = pretty(render(decompose(new XJ())))
    println(js2)

    val ss = Helper.quote(new XJ().map("key"))(new java.lang.StringBuilder).toString
    println(ss)
    println(Helper.unquote(ss))

  }

}
