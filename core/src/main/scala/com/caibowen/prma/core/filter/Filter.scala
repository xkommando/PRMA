package com.caibowen.prma.core.filter

import com.caibowen.prma.core.LifeCycle

/**
* @author BowenCai
* @since  01/12/2014.
*/
trait Filter[T] extends LifeCycle {

  var next: Filter[T] = _
  protected def doAccept(v: T): Int = -1

  def accept(v: T): Int = {
    if (1 == doAccept(v)) {
      if (next != null)
        next.accept(v)
      else 1

    } else -1

  }

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
