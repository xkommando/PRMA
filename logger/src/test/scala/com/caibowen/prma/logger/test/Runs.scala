package com.caibowen.prma.logger.test

import com.zaxxer.hikari.HikariDataSource
import org.junit.Test

/**
 * @author BowenCai
 * @since  16/12/2014.
 */
class Runs {

  @Test
  def r1(): Unit = {
    val klass = new HikariDataSource().getClass

    val c1 = klass.getConstructor()
//    val c2 = klass.getDeclaredConstructor(null)
    val ctor = klass.getDeclaredConstructor()

    if (!ctor.isAccessible) ctor.setAccessible(true)

    val ins = ctor.newInstance()

    println(Long.MaxValue)

  }
}
