package com.caibowen.prma.store.rdb

import javax.sql.DataSource

import akka.actor.{ActorRef, ActorRefFactory}
import com.caibowen.gplume.misc.Str.Utils.notBlank
import com.caibowen.prma.core.{ActorBuilder, StrLoader}

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  10/12/2014.
 */
object StoreBuilder {
  val defaultName = "PRMA.Store.Actor"
}
class StoreBuilder extends ActorBuilder {

  @BeanProperty var actorName: String = _

  @BeanProperty var dataSource: DataSource = _

  @BeanProperty var sqls: StrLoader = _

  @BeanProperty var eventAux: EventStoreAux = _

  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  override def buildWith(sys: ActorRefFactory): ActorRef = {
    val props = RdbEventStore.prop(dataSource, sqls, eventAux, stackStore)
    val _actorName = if (notBlank(actorName)) actorName
                  else StoreBuilder.defaultName
    sys.actorOf(props, _actorName)
  }

}
