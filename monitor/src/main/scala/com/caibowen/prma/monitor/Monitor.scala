package com.caibowen.prma.monitor

import java.util.{List => JList, Map => JMap}

import akka.actor.Actor
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.monitor.eval.Evaluator
import com.caibowen.prma.monitor.notify.Notifier
import org.slf4j.LoggerFactory

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
      var result: String = null
      try {
        result = evaluator.eval(vo)
      } catch {
        case e: Throwable => this.LOG.error(s"Could not evaluate [$vo] with evaluator [$evaluator]", e)
      }
      result match {
        case Evaluator.ACCEPT => allNotifiers.foreach(_ send vo)
        case name: String => {
          val notifier = notifierMap(result)
          if (notifier != null)
            notifier send vo
          else LOG.warn(s"Could not find notifier named [$result] for event [$vo]");
        }
      }
    }
    case x => unhandled(x)
  }

}
