package com.caibowen.prma.api.test

import com.caibowen.prma.api.LogLevel

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
object LevelTest extends App {

  val level = LogLevel.ERROR
  println(level.id)
  println(LogLevel.maxId)
  println(LogLevel.DEBUG)
  println(LogLevel.DEBUG.toString)
  println(LogLevel(6))
  println(LogLevel.from("Debug"))
}
