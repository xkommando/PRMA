package com.caibowen.prma.monitor.notify

import com.caibowen.prma.api.model.EventVO

import scala.beans.BeanProperty


/**
 * @author BowenCai
 * @since  01/12/2014.
 */
trait Notifier {

  @BeanProperty var name: String = _

  def send(vo: EventVO): Unit
}
