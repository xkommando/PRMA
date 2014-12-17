package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  30/11/2014.
 */
sealed trait Response
case object NotifyAll extends Response
case object Reject extends Response
case class NotifyOne(name: String) extends Response

// response that carries extra data for the notifier
class ResponseData[T](name: String, values: T) extends NotifyOne(name)

trait Evaluator {
  def eval(vo : EventVO): Response
}
