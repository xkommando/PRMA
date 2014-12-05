package com.caibowen.prma.store

import javax.annotation.Nonnull

import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
trait EventStore {

  def persist(@Nonnull event: EventVO): Long

//  def batchPersist(@Nonnull ls: List[EventVO])
}
