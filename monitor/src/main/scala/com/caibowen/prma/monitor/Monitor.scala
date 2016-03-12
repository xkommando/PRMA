package com.caibowen.prma.monitor

import java.util.{List => JList, Map => JMap}

import akka.AkkaException
import akka.actor._
import akka.actor.SupervisorStrategy._
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.monitor.eval._
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

  final val defaultDecider: SupervisorStrategy.Decider = {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: DeathPactException => Stop
    case _: AkkaException => Restart
    case _: Exception => Escalate
  }
  final val defaultStrategy = new OneForOneStrategy(defaultDecider)
}
@SerialVersionUID(7376528395328272879L)
class Monitor(val evaluator: Evaluator, val notifierMap: Map[String, Notifier]) extends Actor with ActorLogging{

  override val supervisorStrategy = Monitor.defaultStrategy

  private[this] val allNotifiers = notifierMap.values.toList

  def receive = {
    case vo: EventVO => {
      Try(evaluator.eval(vo)) match {
        case Success(response) => response match {
          case n: NotifyOne =>
            val notifier = notifierMap get n.name
            if (notifier.isDefined)
              notifier.get.take(n)
            else log.warning(s"Could not find notifier named [${n.name}] on event [$vo]")
          case n: Response => allNotifiers.foreach(_ take n)
        }

        case Failure(e) => log.error(s"Could not evaluate [$vo] with evaluator [$evaluator]", e)
      }
    }

    case x => unhandled(x)
  }

}
