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

  @BeanProperty var exceptMsgStore: KVStore[Int, String] = _
  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  @inline
  protected def putStr(store: KVStore[Int, String], str: String) =
    if (store.put(str.hashCode, str))
      throw new JdbcException(s"could not save[$str] with[$store]")


  // cached sql ref
  final val _putExcept = sqls.get("ExceptionDAO.putExcept")
  final val _putExceptR = sqls.get("ExceptionDAO.putEventExceptRelation")
  final val _putExceptStackR = sqls.get("ExceptionDAO.putStackTraceExceptRelation")

  def putExceptions(eventId: Long, exceps: List[ExceptionVO]): Unit = {
    val nexps = exceps.filter(!hasException(_))

    val msgs = nexps.filter(_.message.isDefined)
      .map(t=>t.message.get.hashCode -> t.message.get)

    exceptMsgStore.putIfAbsent(msgs)

    val newStack = List.newBuilder[StackTraceElement]
    newStack.sizeHint(32)

    // insert exception
    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putExcept)
        for (exp <- nexps) {
          ps.setLong(1, exp.id)
          ps.setString(2, exp.name)

          if (exp.message.isDefined)
            ps.setInt(3, exp.message.get.hashCode)
          else ps.setNull(3, Types.INTEGER)

          if (exp.stackTraces.isDefined)
            newStack ++= exp.stackTraces.get

          ps.addBatch
        }
        ps
      }, null, null)

    // insert new stack traces
    stackStore.putIfAbsent(newStack.result().map(t=>t.hashCode()->t))

    // insert relations with stack traces
    batchInsert((con: Connection) => {
      val ps = con.prepareStatement(_putExceptStackR)
      for (exp <- nexps if exp.stackTraces.isDefined) {
        val traces = exp.stackTraces.get
        var i = 0
        for (straceElem <- traces) {
          ps.setInt(1, i)
          ps.setInt(2, straceElem.hashCode())
          ps.setLong(3, exp.id)
          ps.addBatch()
          i += 1
        }
      }
      ps
    }, null, null)


    // insert relations with Event
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
