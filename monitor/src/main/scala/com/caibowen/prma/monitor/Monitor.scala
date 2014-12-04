package com.caibowen.prma.monitor

import java.util.concurrent.{CopyOnWriteArrayList, Executor}

import com.caibowen.gplume.annotation.{NoExcept, Const}
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.monitor.eval.Evaluator
import com.caibowen.prma.monitor.notify.Notifier
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty
import scala.collection.mutable

/**
* @author BowenCai
* @since  30/11/2014.
*/
@SerialVersionUID(7376528395328272879L)
class Monitor(executor: Executor, evaluator: Evaluator) extends Serializable {

  private[this] val LOG = LoggerFactory.getLogger(classOf[Monitor])

  def this(executor: Executor, evaluator: Evaluator, notis: java.util.List[Notifier]) {
    this(executor, evaluator)
    setNotifiers(notis)
  }

  private[this] val notifiers = new mutable.HashMap[String, Notifier]

  def setNotifiers(ns: java.util.List[Notifier]): Unit = {
    import scala.collection.JavaConversions.asScalaBuffer
    for (n <- ns)
      notifiers += (n.name -> n)
  }

  @Const
  def recieve(vo: EventVO): Unit = {
    var result: String = null
    try {
      result = evaluator.eval(vo)
    } catch {
      case ex => this.LOG.error(s"Could not evaluate [$vo] with evaluator [$evaluator]", ex)
    }
    result match {
      case Evaluator.ACCEPT => {
        executor.execute(new Runnable {
          override def run(): Unit = {
            for ((name, noti) <- notifiers) {
              try {
                noti.send(vo)
              }
              catch {
                case e => {
                  LOG.error(s"Could not notify [$vo] with notifier [$name]", e)
                }
              }
            }

          }
        })
      }
      case name: String => {
        val noti = notifiers(result)
        if (noti != null)
          executor.execute(new Runnable {
            override def run(): Unit = {
              try {
                noti.send(vo)
              } catch {
                case ex =>
                  LOG.error(s"Could not notify [$vo] with notifier [${noti.name}]", ex);
              }
            }
          })
        else LOG.warn(s"Could not find notifier named [$result] for event [$vo]");
      }
    }
  }

}
