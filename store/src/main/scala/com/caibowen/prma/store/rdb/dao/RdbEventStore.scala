package com.caibowen.prma.store.rdb.dao

import java.sql.{Connection, Types, PreparedStatement, SQLException}
import javax.annotation.Nonnull

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{StatementCreator, JdbcException, JdbcSupport}
import com.caibowen.gplume.jdbc.transaction.{Transaction, TransactionCallback}
import com.caibowen.prma.api.model.EventVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.EventStore

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class RdbEventStore(private[this] val sqls: StrLoader) extends JdbcSupport with EventStore {

  @BeanProperty var eventAux: EventStoreAux = _

  @BeanProperty var loggerNameStore: KVStore[Int, String] = _
  @BeanProperty var threadStore: KVStore[Int, String] = _
  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  override def persist(@Nonnull event: EventVO): Long = {
    execute(new TransactionCallback[Unit] {
      override def withTransaction(transaction: Transaction): Unit = {
        // do
        val id = putEvent(event)

        if (event.markers.isDefined)
          eventAux.putMarkers(id, event.markers.get)

        if (event.exceptions.isDefined)
          eventAux.putExceptions(id, event.exceptions.get)

        if (event.properties.isDefined)
          eventAux.putProperties(id, event.properties.get)
      }
    })
  }

  @inline
  protected def putStr(store: KVStore[Int, String], str: String) =
    if (store.put(str.hashCode, str))
      throw new JdbcException(s"could not save[$str] with[$store]")

  protected def putEvent(event: EventVO): Long = {

    putStr(loggerNameStore, event.loggerName)
    putStr(threadStore, event.threadName)

    val st = event.callerStackTrace
    if (stackStore.put(st.hashCode(), st))
      throw new JdbcException(s"Could not insert stacktrace[$st] ")

    insert(new StatementCreator {
      @throws(classOf[SQLException])
      def createStatement(con: Connection): PreparedStatement = {
        val ps: PreparedStatement =
          con.prepareStatement(sqls.get("EventDAO.insert"),
            RdbEventStore.AUTO_GEN_ID)

        ps.setLong(1, event.timeCreated)
        ps.setByte(2, event.level.id.toByte)
        ps.setInt(3, event.loggerName.hashCode)
        ps.setInt(4, event.threadName.hashCode)
        ps.setInt(5, event.callerStackTrace.hashCode())
        ps.setLong(6, event.flag)
        ps.setString(7, event.message)
        if (event.reserved.isDefined) ps.setLong(8, event.reserved.get)
        else ps.setNull(8, Types.BIGINT)

        return ps
      }
    }, RdbEventStore.AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
  }

//  override def batchPersist(@Nonnull ls: List[EventVO]) = {
//
//  }
}
object RdbEventStore{
  val AUTO_GEN_ID: Array[String] = Array("id")
}
