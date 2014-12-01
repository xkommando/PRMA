package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  30/11/2014.
 */
object Evaluator{
  val ACCEPT: String = classOf[Evaluator].getName + ".ACCEPT"
  val REJECT: String = classOf[Evaluator].getName + ".REJECT"
}
trait Evaluator {
  def eval(vo : EventVO): String
}
