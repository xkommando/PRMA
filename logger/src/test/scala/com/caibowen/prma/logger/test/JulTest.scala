package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import java.util.Date
import java.util.logging.{ConsoleHandler, Level => JulLevel, LogRecord => JulLogRecord, Logger => JulLogger}

import com.caibowen.prma.logger.jul.JulRecordAdaptor
import org.junit.Test

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
class JulTest extends DBContext {

  val LOG = JulLogger.getLogger(classOf[JulTest].getName)
  val adaptor = new JulRecordAdaptor

  Console.setOut(System.err)

  @Test
  def layout(): Unit = {
    val params = new Array[AnyRef](3)
    params(0) = 111.asInstanceOf[AnyRef]
    params(1) = 222.asInstanceOf[AnyRef]
    params(2) = new Date()
    val _h = new ConsoleHandler
    _h.setLevel(JulLevel.ALL)
    LOG.addHandler(_h)
    LOG.setLevel(JulLevel.ALL)
//    LOG.log(JulLevel.FINE, "{0}msg{1} haha {2}", params)

    val exp = new RuntimeException("msg level 3", new IOException("msg level 2", new FileNotFoundException("msg level 1")))

    val record = new JulLogRecord(JulLevel.FINE, "{0}msg{1} haha {2}")
    record.setParameters(params)
    record.setThrown(exp)

    val vo = adaptor.from(record)
    println(vo)
    println("-----------------------")
    eventStore ! vo


//    println(JSON.toJSONString(vo.asInstanceOf[AnyRef], true))
//    println(java.text.MessageFormat.format("{0}msg{1} haha {2}", 1.asInstanceOf[AnyRef], 2.asInstanceOf[AnyRef], new Date()))
  }
}
