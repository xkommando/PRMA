package com.caibowen.prma.monitor.eval

import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.filter.StrFilter

/**
 * @author BowenCai
 * @since  30/11/2014.
 */
class MsgEval(filter: StrFilter) extends Evaluator {

  override def eval(vo: EventVO): String = {
    if (filter.accept(vo.loggerName) == 1)
      Evaluator.ACCEPT
    else
      Evaluator.REJECT
  }
}
