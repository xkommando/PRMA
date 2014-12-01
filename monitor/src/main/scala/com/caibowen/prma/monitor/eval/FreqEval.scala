package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.FreqCounter

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  30/11/2014.
 */
class FreqEval(eval: Evaluator, counter: FreqCounter) extends Evaluator {
  @BeanProperty var limit: Double = 1.0

  override def eval(vo: EventVO): String = {
    val ret = eval.eval(vo)
    if (!Evaluator.REJECT.eq(ret)) {
      counter.count()
      if (counter.freq >= limit)
        return ret
    }
    Evaluator.REJECT
  }

}
