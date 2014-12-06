package com.caibowen.prma.store.rdb.impl

import java.sql.{Connection, PreparedStatement, SQLException, Types}
import javax.annotation.Nonnull

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcException, JdbcSupport, StatementCreator}
import com.caibowen.gplume.misc.Bytes
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.rdb.{EventStoreAux, KVStore}

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class StoreAuxImpl(private[this] val sqls: StrLoader) extends JdbcSupport with EventStoreAux {

  @BeanProperty var exceptNameStore: KVStore[Int, String] = _
  @BeanProperty var exceptMsgStore: KVStore[Int, String] = _
  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  @inline
  protected def putStr(store: KVStore[Int, String], str: String) =
    if (store.put(str.hashCode, str))
      throw new JdbcException(s"could not save[$str] with[$store]")


  def putExceptions(eventId: Long, exceps: List[ExceptionVO]): Unit = {
    val nexps = exceps.filter(!hasException(_))

    exceptNameStore.putIfAbsent(nexps.map(t=>t.exceptionName.hashCode -> t.exceptionName))
    exceptMsgStore.putIfAbsent(nexps.filter(_.exceptionMessage.isDefined)
                        .map(t=>t.exceptionMessage.get.hashCode -> t.exceptionMessage.get))

    val newStack = List.newBuilder[StackTraceElement]
    newStack.sizeHint(32)

    // insert exception
    batchInsert(new StatementCreator {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement(sqls.get("ExceptionDAO.putExcept"))
        for (exp <- nexps) {
          ps.setLong(1, exp.id)
          ps.setInt(2, exp.exceptionName.hashCode)

          if (exp.exceptionMessage.isDefined)
            ps.setInt(3, exp.exceptionMessage.get.hashCode)
          else ps.setNull(3, Types.INTEGER)

          if (exp.stackTraces.isDefined) {
            val ls = exp.stackTraces.get
            newStack ++= ls

            val arr = ls.map(_.hashCode()).toArray
            val buf: Array[Byte] = Bytes.ints2bytes(arr)
            ps.setBytes(4, buf)
          }
          else ps.setNull(4, Types.BINARY)

          ps.addBatch
        }
        ps
      }
    }, null, null)

    // insert new stack
    stackStore.putIfAbsent(newStack.result().map(t=>t.hashCode()->t))

    // insert relations
    batchInsert(new StatementCreator {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement(sqls.get("ExceptionDAO.putRelation"))
        var i = 0
        for (vo <- exceps) {
          ps.setInt(1, i)
          ps.setLong(2, eventId)
          ps.setLong(3, vo.id)
          ps.addBatch
          i += 1
        }
        ps
      }
    }, null,null)

  }


  def putProperties(eventId: Long, props: Map[String, String]): Unit = {
    val newPs = props.filterKeys(!hasProperty(_))

    batchInsert(new StatementCreator {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement(sqls.get("PropertyDAO.putMap"))
        for ((k,v) <- newPs) {
          ps.setInt(1, k.hashCode);
          ps.setString(2, k);
          ps.setBytes(3, v.getBytes);
          ps.addBatch();
        }
        return ps;
      }
    }, null, null)

    batchInsert(new StatementCreator {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement(
          sqls.get("PropertyDAO.putRelation"))
        for ((k, v) <- props) {
          ps.setInt(1, k.hashCode);
          ps.setLong(2, eventId);
          ps.addBatch();
        }
        ps
      }
    }, null, null)
  }


  def putMarkers(eventId: Long, mks: Set[String]) {
    val newMk = mks.filter(!hasMarker(_))
    batchInsert( new StatementCreator {
      @throws(classOf[SQLException])
      def createStatement(con: Connection): PreparedStatement = {
        val ps: PreparedStatement = con.prepareStatement(sqls.get("MarkerDAO.putMarker"))
        for (e <- newMk) {
          ps.setInt(1, e.hashCode)
          ps.setString(2, e)
          ps.addBatch
        }
        ps
      }
    }, null, null)
    batchInsert( new StatementCreator {
      @Nonnull
      @throws(classOf[SQLException])
      def createStatement(con: Connection): PreparedStatement = {
        val ps: PreparedStatement = con.prepareStatement(sqls.get("MarkerDAO.putRelation"))
        for (e <- mks) {
          ps.setInt(1, e.hashCode)
          ps.setLong(2, eventId)
          ps.addBatch
        }
        ps
      }
    }, null, null)
  }





  def hasException(vo: ExceptionVO): Boolean = {
    queryForObject(new StatementCreator() {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT count(1) FROM `exception` WHERE id =?")
        ps.setLong(1, vo.id)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }

  def hasProperty(key: String): Boolean = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT count(1) FROM `property` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }

  def hasMarker(key: String): Boolean = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT count(1) FROM `marker_name` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }
}
