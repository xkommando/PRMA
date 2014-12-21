package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO


/**
 * @author BowenCai
 * @since  30/11/2014.
 */

class Response(val event: EventVO)
case object Reject extends Response(null)
class NotifyOne(val name: String, event: EventVO) extends Response(event)

// response that carries extra data for the notifier
trait WithData[T] {val data: T}

class NotifyAllWith[T](val data: T, event: EventVO) extends Response(event) with WithData[T]

class NotifyWith[T](name: String, val data: T, event: EventVO) extends NotifyOne(name, event) with WithData[T]

trait Evaluator {
  def eval(vo : EventVO): Response = Reject
}
