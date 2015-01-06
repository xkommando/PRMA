package com.caibowen.prma.api

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
object LogLevel extends Enumeration {
  type LogLevel = Value
  val OFF = Value(32, "OFF")
  val FATAL = Value(16, "FATAL")
  /* MAIN */
  val ERROR = Value(8, "ERROR")
  val WARN = Value(6, "WARN")
  val INFO = Value(4, "INFO")
  val DEBUG = Value(2, "DEBUG")
  val TRACE = Value(1, "TRACE")
  /* MAIN */
  val ALL = Value(0, "ALL")

  def from(name: Int): LogLevel = {
    super.apply(name)
  }

  def from(name: String): LogLevel = {
    val tname = name.trim.toUpperCase
    super.withName(tname)
  }
}
