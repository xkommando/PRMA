package com.caibowen.prma.store.rdb

import java.sql.{Connection, PreparedStatement, SQLException, Types}
import javax.annotation.Nonnull
import javax.sql.DataSource
import akka.actor.Props
import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.transaction.{Transaction, TransactionCallback}
import com.caibowen.gplume.jdbc.{JdbcSupport, StatementCreator}
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.EventStore

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class RdbEventStore private[this] (dataSource: DataSource,
                                    sqls: StrLoader,
                                   eventAux: EventStoreAux,
                                   stackStore: KVStore[Int,StackTraceElement],
                                    loggerNameStore: KVStore[Int,String],
                                    threadNameStore: KVStore[Int,String]) extends JdbcSupport(dataSource) with EventStore {


  override def put(@Nonnull event: EventVO): Long = {
    execute(new TransactionCallback[Long] {
      override def withTransaction(transaction: Transaction): Long = {
        // do
        val id = putEvent(event)

        if (event.tags.isDefined)
          eventAux.putTags(id, event.tags.get)

        if (event.exceptions.isDefined)
          eventAux.putExceptions(id, event.exceptions.get)

        if (event.properties.isDefined)
          eventAux.putProperties(id, event.properties.get.asInstanceOf[Map[String, String]])

        id
      }
    })
  }

  final val _putEvent = sqls.get("EventStore.insert")
  protected def putEvent(event: EventVO): Long = {

    val st = event.callerStackTrace
    stackStore.put(st.hashCode(), st)
    val ln = event.loggerName
    loggerNameStore.put(ln.hashCode, ln)
    val thread = event.threadName
    threadNameStore.put(thread.hashCode, thread)

    insert(new StatementCreator {
      @throws(classOf[SQLException])
      def createStatement(con: Connection): PreparedStatement = {
        val ps: PreparedStatement =
          con.prepareStatement(_putEvent, RdbEventStore.AUTO_GEN_ID)

        ps.setLong(1, event.timeCreated)
        ps.setByte(2, event.level.id.toByte)
        ps.setInt(3, ln.hashCode)//
        ps.setInt(4, thread.hashCode)//
        ps.setInt(5, st.hashCode())
        ps.setLong(6, event.flag)
        ps.setString(7, event.message)
        if (event.reserved.isDefined) ps.setLong(8, event.reserved.get)
        else ps.setNull(8, Types.BIGINT)
        ps
      }
    }, RdbEventStore.AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
  }

}
object RdbEventStore{
  val AUTO_GEN_ID: Array[String] = Array("id")
  def prop(dataSource: DataSource,
           sqls: StrLoader,
           eventAux: EventStoreAux,
           stackStore: KVStore[Int,StackTraceElement],
           loggerNameStore: KVStore[Int,String],
           threadNameStore: KVStore[Int,String])

  = Props(classOf[RdbEventStore], dataSource, sqls, eventAux, stackStore, loggerNameStore, threadNameStore)
}
