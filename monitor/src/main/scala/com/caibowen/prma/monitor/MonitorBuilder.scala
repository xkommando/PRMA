package com.caibowen.prma.monitor

import java.util.concurrent.atomic.AtomicInteger
import java.util.{List => JList, Map => JMap}

import akka.actor.{ActorRef, ActorRefFactory}
import com.caibowen.gplume.scala.conversion.StrConversions.ApplyConversion
import com.caibowen.prma.core.ActorBuilder
import com.caibowen.prma.monitor.eval.Evaluator
import com.caibowen.prma.monitor.notify.Notifier

import scala.beans.BeanProperty
import scala.collection.JavaConversions

/*
Example:

    <bean class="com.myapp.MyMonitor">
        <prop name="evaluator" ref="eval"/>
        <prop name="notifiers">
            <list>
                <ref>notifier1</ref>
                <bean class="myMonifier"/>
            </list>
        </prop>
    </bean>
 */

/**
 * helper class for Gplume injection
 *
 *
 * @author BowenCai
 * @si: nce  10/12/2014.
 */
object MonitorBuilder{
  private val anonymousCount = new AtomicInteger(0)
  def newName =
    "PRMA.Monitor-" + anonymousCount.incrementAndGet().toString
}

class MonitorBuilder extends ActorBuilder {

  @BeanProperty var name: String = _
  @BeanProperty var evaluator: Evaluator = _

  @BeanProperty var notifiers: JList[Notifier] = _

  override def buildWith(factory: ActorRefFactory): ActorRef = {

    val notifierMap = JavaConversions.asScalaBuffer(notifiers)
          .map(e=> (e.name -> e)).toMap

    val props = Monitor.prop(evaluator, notifierMap)

    val monitorName: String = if (name.notBlank) name
      else MonitorBuilder.newName

    factory.actorOf(props, monitorName)
  }


}
