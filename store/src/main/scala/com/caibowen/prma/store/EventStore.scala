package com.caibowen.prma.store

import javax.annotation.Nonnull

import akka.AkkaException
import akka.actor._
import akka.actor.SupervisorStrategy._
import com.caibowen.prma.api.model.EventVO

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
object EventStore {
  final val defaultDecider: SupervisorStrategy.Decider = {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: DeathPactException => Stop
    case _: AkkaException => Restart
    case _: Exception => Escalate
  }
  final val defaultStrategy = new OneForOneStrategy(defaultDecider)
}
trait EventStore extends Actor with ActorLogging {

  override val supervisorStrategy = EventStore.defaultStrategy

  def put(@Nonnull event: EventVO): Long

  def receive = {
    case vo: EventVO => put(vo)
    case x => unhandled(x)
  }
}
