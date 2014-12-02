package com.caibowen.prma.core

/**
 * @author BowenCai
 * @since  01/12/2014.
 */
trait LifeCycle {

  protected[this] var started = false

  def start(): Unit = {
    started = true
  }
  def stop(): Unit = {
    started = false
  }

  def isStarted: Boolean = {
    started
  }
}
