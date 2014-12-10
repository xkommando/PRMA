package com.caibowen.prma.store

import javax.annotation.Nonnull

import akka.actor.Actor
import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
trait EventStore extends Actor {

  def put(@Nonnull event: EventVO): Long

//  def batchPersist(@Nonnull ls: List[EventVO])

  def receive = {
    case vo: EventVO => put(vo)
    case x => unhandled(x)
  }
}
