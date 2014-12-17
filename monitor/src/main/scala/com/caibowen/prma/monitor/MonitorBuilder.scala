package com.caibowen.prma.monitor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorRefFactory}
import com.caibowen.gplume.scala.conversion.StrConversions.ApplyConversion
import com.caibowen.prma.core.ActorBuilder
import com.caibowen.prma.monitor.eval.Evaluator
import com.caibowen.prma.monitor.notify.Notifier

import scala.beans.BeanProperty

/*
Example:

    <bean class="com.caibowen.prma.monitor.MonitorBuilder">
        <prop name="evaluator" ref="eval"/>
        <prop name="notifiers">
            <list>
                <ref>notifier1</ref>
                <bean class="myNonifier"/>
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
  private[monitor] def newName =
    "PRMA.Monitor.Actor_No." + anonymousCount.incrementAndGet().toString
}
class MonitorBuilder extends ActorBuilder {

  @BeanProperty var name: String = _
  @BeanProperty var evaluator: Evaluator = _

  @BeanProperty var notifiers: List[Notifier] = _

  override def buildWith(factory: ActorRefFactory): ActorRef = {

    val notifierMap = notifiers.map(e=> (e.name -> e)).toMap

    val props = Monitor.prop(evaluator, notifierMap)

    val monitorName: String = if (name.notBlank) ActorBuilder.validName(name)
      else MonitorBuilder.newName

    factory.actorOf(props, monitorName)
  }


}
