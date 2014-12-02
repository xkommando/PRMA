package com.caibowen.prma.api

import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
trait EventAdaptor[V] {

  def from(otherEvent: V): EventVO

  def to(vo: EventVO): V
}
