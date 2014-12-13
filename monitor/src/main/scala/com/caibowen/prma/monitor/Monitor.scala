package com.caibowen.prma.monitor

import java.util.{List => JList, Map => JMap}

import akka.actor.{Actor,Props}
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.monitor.eval.Evaluator
import com.caibowen.prma.monitor.notify.Notifier
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
* @author BowenCai
* @since  30/11/2014.
*/
object Monitor {
  def prop(evaluator: Evaluator, notifierMap: Map[String, Notifier])
   = Props(new Monitor(evaluator, notifierMap))
}

@SerialVersionUID(7376528395328272879L)
class Monitor(val evaluator: Evaluator, val notifierMap: Map[String, Notifier]) extends Actor {

  private[this] val LOG = LoggerFactory.getLogger(classOf[Monitor])

  private[this] val allNotifiers = notifierMap.values.toList

  def receive = {
    case vo: EventVO => {
      Try(evaluator.eval(vo)) match {
        case Success(name) => name match {
          case Evaluator.ACCEPT => allNotifiers.foreach(_ send vo)
          case name: String => {
            val notifier = notifierMap(name)
            if (notifier != null)
              notifier send vo
            else LOG.warn(s"Could not find notifier named [$name] on event [$vo]")
          }
          case null => LOG.error(s"evaluator [$evaluator] returns null on event [$vo]")
        }

        case Failure(e) => this.LOG.error(s"Could not evaluate [$vo] with evaluator [$evaluator]", e)
      }
    }

    case x => unhandled(x)
  }

}
