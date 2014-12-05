package com.caibowen.prma.logger.test

import java.util

import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.core.StrLoader

/**
 * @author BowenCai
 * @since  03/12/2014.
 */
object TesteEval extends App {

  val event = new ILogEventTest().gen()
  val ls = new util.ArrayList[String](8)
  ls.add("query1_mysql.xml")
  val ld = new StrLoader(ls)
  ld.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass.getClassLoader))
  ld.start()
  println(ld.get("emp"))
}
