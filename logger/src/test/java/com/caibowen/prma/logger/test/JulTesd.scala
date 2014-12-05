package com.caibowen.prma.logger.test

import java.io.{FileNotFoundException, IOException}
import java.util.Date
import java.util.logging.{Level, LogRecord, Handler, Logger}

import com.caibowen.gplume.misc.Str
import com.caibowen.prma.logger.jul.JulRecordAdaptor

/**
 * @author BowenCai
 * @since  04/12/2014.
 */

object JulTesd extends App {
  implicit class SSS(s: String) {
    def notBlank = Str.Utils.notBlank(s)
  }
  val ss1: String = null
  val ss2: String = "   "
  println(ss1.notBlank)
  println(ss2.notBlank)

  val log = Logger.getLogger("jul logger")
  log.addHandler(new Handler {override def flush(): Unit = ???

    override def publish(record: LogRecord): Unit = {
      val e = new JulRecordAdaptor().from(record)
      println(e)
    }

    override def close(): Unit = {

    }
  })
  val _fk = new RuntimeException("msg level 3",
                new IOException("msg level 2",
                  new FileNotFoundException("msg level 1")))

  log.setLevel(Level.ALL)
  log.log(Level.FINER, "I am fine, haha", _fk)

//  public void entering(String sourceClass, String sourceMethod, Object params[]) {
//  println(JulRecordAdaptor.commonFrames(_fk, _fk.getCause))
  val _ps : Array[Object] = Array(_fk, new Date(), "")

  log.entering("source class", "src method", _ps)

//  _fk.printStackTrace()
}
