package com.caibowen.prma.core.filter

import com.caibowen.prma.core.LifeCycle

/**
* @author BowenCai
* @since  01/12/2014.
*/
trait Filter[T] extends LifeCycle {

  var next: Filter[T] = _

  @inline
  protected def doAccept(v: T): Int = -1

  def accept(v: T): Int = {
    if (1 == doAccept(v)) {
      if (next != null)
        next.accept(v)
      else 1

    } else -1

  }
//
//  protected[this] var started = false
//
//  @inline
//  def start(): Unit = {
//    started = true
//  }
//
//  @inline
//  def stop(): Unit = {
//    started = false
//  }
//
//  @inline
//  def isStarted: Boolean = {
//    started
//  }

}
