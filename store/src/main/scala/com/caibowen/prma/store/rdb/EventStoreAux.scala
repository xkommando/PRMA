package com.caibowen.prma.store.rdb

import com.caibowen.prma.api.model.ExceptionVO

/**
 * properties
 * exceptions
 * markers
 *
 * @author BowenCai
 * @since  05/12/2014.
 */
trait EventStoreAux {

  def putExceptions(eventId: Long, prop: List[ExceptionVO])

  def putProperties(eventId: Long, prop: Map[String, String])

  def putTags(eventId: Long, prop: Set[String])
}
