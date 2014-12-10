package com.caibowen.prma.store.rdb.impl

import java.sql.{Connection, PreparedStatement, Types}

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcException, JdbcSupport}
import com.caibowen.gplume.misc.Bytes
import com.caibowen.gplume.scala.conversion.CommonConversions._
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


  // cached sql ref
  final val _putExcept = sqls.get("ExceptionDAO.putExcept")
  final val _putExceptR = sqls.get("ExceptionDAO.putRelation")

  def putExceptions(eventId: Long, exceps: List[ExceptionVO]): Unit = {
    val nexps = exceps.filter(!hasException(_))

    exceptNameStore.putIfAbsent(nexps.map(t=>t.exceptionName.hashCode -> t.exceptionName))
    exceptMsgStore.putIfAbsent(nexps.filter(_.exceptionMessage.isDefined)
                        .map(t=>t.exceptionMessage.get.hashCode -> t.exceptionMessage.get))

    val newStack = List.newBuilder[StackTraceElement]
    newStack.sizeHint(32)

    // insert exception
    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putExcept)
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
      }, null, null)

    // insert new stack
    stackStore.putIfAbsent(newStack.result().map(t=>t.hashCode()->t))

    // insert relations
    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putExceptR)
        var i = 0
        for (vo <- exceps) {
          ps.setInt(1, i)
          ps.setLong(2, eventId)
          ps.setLong(3, vo.id)
          ps.addBatch
          i += 1
        }
        ps
      },null,null)
  }

  final val _putProp = sqls.get("PropertyDAO.putMap")
  final val _putPropR = sqls.get("PropertyDAO.putRelation")
  def putProperties(eventId: Long, props: Map[String, String]): Unit = {
    val newPs = props.filterKeys(!hasProperty(_))

    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putProp)
        for ((k,v) <- newPs) {
          ps.setInt(1, k.hashCode);
          ps.setString(2, k);
          ps.setBytes(3, v.getBytes);
          ps.addBatch();
        }
        ps;
      }, null, null)

    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putPropR)
        for ((k, v) <- props) {
          ps.setInt(1, k.hashCode);
          ps.setLong(2, eventId);
          ps.addBatch();
        }
        ps
      }, null, null)
  }


  final val _putMk = sqls.get("MarkerDAO.putMarker")
  final val _putMKR = sqls.get("MarkerDAO.putRelation")
  def putMarkers(eventId: Long, mks: Set[String]) {
    val newMk = mks.filter(!hasMarker(_))
    batchInsert((con: Connection) => {
        val ps: PreparedStatement = con.prepareStatement(_putMk)
        for (e <- newMk) {
          ps.setInt(1, e.hashCode)
          ps.setString(2, e)
          ps.addBatch
        }
        ps
      },null, null)

    batchInsert((con: Connection) => {
        val ps: PreparedStatement = con.prepareStatement(_putMKR)
        for (e <- mks) {
          ps.setInt(1, e.hashCode)
          ps.setLong(2, eventId)
          ps.addBatch
        }
        ps
      }, null, null)
  }

  def hasException(vo: ExceptionVO): Boolean =
    queryForObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `exception` WHERE id =?")
        ps.setLong(1, vo.id)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

  def hasProperty(key: String): Boolean =
    queryForObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `property` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

  def hasMarker(key: String): Boolean =
    queryForObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `marker_name` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

}
