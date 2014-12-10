package com.caibowen.prma.store.rdb

import javax.sql.DataSource

import akka.actor.{ActorRefFactory, ActorRef}
import com.caibowen.gplume.misc.Str.Utils.notBlank
import com.caibowen.prma.core.{ActorBuilder, StrLoader}
import com.caibowen.prma.store.EventStore

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  10/12/2014.
 */
object StoreBuilder {
  val defaultName = classOf[EventStore].getName + ".Actor"
}
class StoreBuilder extends ActorBuilder {

  @BeanProperty var actorName: String = _

  @BeanProperty var dataSource: DataSource = _

  @BeanProperty var sqls: StrLoader = _

  @BeanProperty var eventAux: EventStoreAux = _

  @BeanProperty var loggerNameStore: KVStore[Int, String] = _
  @BeanProperty var threadStore: KVStore[Int, String] = _
  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  override def buildWith(sys: ActorRefFactory): ActorRef = {
    val props = RdbEventStore.prop(dataSource, sqls, eventAux, loggerNameStore, threadStore, stackStore)
    val _actorName = if (notBlank(actorName)) actorName
                  else StoreBuilder.defaultName
    sys.actorOf(props, _actorName)
  }

}
