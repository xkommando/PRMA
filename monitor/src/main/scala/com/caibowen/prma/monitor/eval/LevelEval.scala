package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.EventVO

/**
 *
 * @author BowenCai
 * @since  30/11/2014.
 */
class LevelEval(lowerBound: Int,
                upperBound: Int) extends Evaluator {

  def this(lowerBound: String, upperBound: String){
    this(LogLevel.from(lowerBound).id, LogLevel.from(upperBound).id)
  }

  @inline
  override def eval(vo : EventVO): Response = {
    val level = vo.level.id
    if (lowerBound <= level && level <= upperBound)
      new Response(vo)
    else
      Reject
  }

}
