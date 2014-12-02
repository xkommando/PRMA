package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  30/11/2014.
 */
class LevelEval(lowerBound: Int, upperBound: Int) extends Evaluator {

  @inline
  override def eval(vo : EventVO): String = {
    val level = vo.level.id
    if (lowerBound <= level && level <= upperBound)
      Evaluator.ACCEPT
    else
      Evaluator.REJECT
  }

}
