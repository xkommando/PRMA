package com.caibowen.prma.monitor.notify


import com.caibowen.prma.monitor.eval.Response
import scala.beans.BeanProperty


/**
 * @author BowenCai
 * @since  01/12/2014.
 */
trait Notifier {

  @BeanProperty var name: String = _

  def take(vo: Response): Unit
}
