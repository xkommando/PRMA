package com.caibowen.prma.store.rdb.impl

import java.sql.{Connection, PreparedStatement, Types}

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcException, JdbcSupport}
import gplume.scala.conversion.CommonConversions._
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.rdb.{EventStoreAux, KVStore}

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class StoreAuxImpl(private[this] val sqls: StrLoader) extends JdbcSupport with EventStoreAux {

  @BeanProperty var stackStore: KVStore[Int,StackTraceElement] = _

  @inline
  protected def putStr(store: KVStore[Int, String], str: String) =
    if (store.put(str.hashCode, str))
      throw new JdbcException(s"could not save[$str] with[$store]")


  // cached sql ref
  final val _putExcept = sqls.get("Exception.putExcept")
  final val _putExceptREvent = sqls.get("Exception.putRelationEvent")
  final val _putExceptRStackTrace = sqls.get("Exception.putRelationStackTrace")

  def putExceptions(eventId: Long, exceps: List[ExceptionVO]): Unit = {
    val nexps = exceps.filter(!hasException(_))

    val newStack = List.newBuilder[StackTraceElement]
    newStack.sizeHint(32)

    // insert exception
    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putExcept)
        for (exp <- nexps) {
          ps.setLong(1, exp.id)
          ps.setString(2, exp.name)

          if (exp.message.isDefined)
            ps.setString(3, exp.message.get)
          else ps.setNull(3, Types.VARCHAR)

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
      val ps = con.prepareStatement(_putExceptRStackTrace)
      for (except <- nexps if except.stackTraces.isDefined) {
        val stackTraces = except.stackTraces.get
        var i = 0
        for (elem <- stackTraces) {
          ps.setInt(1, i)
          ps.setInt(2, elem.hashCode())
          ps.setLong(3, except.id)
          ps.addBatch()
          i += 1
        }
      }
      ps
    }, null, null)


    // insert relations with Event
    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putExceptREvent)
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

  final val _putProp = sqls.get("Property.putMap")
  final val _putPropR = sqls.get("Property.putRelation")
  def putProperties(eventId: Long, props: Map[String, String]): Unit = {
    val newPs = props.filterKeys(!hasProperty(_))

    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putProp)
        for ((k,v) <- newPs) {
          ps.setInt(1, k.hashCode)
          ps.setString(2, k)
          ps.setBytes(3, v.getBytes)
          ps.addBatch()
        }
        ps
      }, null, null)

    batchInsert((con: Connection) => {
        val ps = con.prepareStatement(_putPropR)
        for ((k, v) <- props) {
          ps.setInt(1, k.hashCode)
          ps.setLong(2, eventId)
          ps.addBatch()
        }
        ps
      }, null, null)
  }


  final val _putTags = sqls.get("Tag.putTags")
  final val _putTagR = sqls.get("Tag.putRelation")
  def putTags(eventId: Long, tags: Set[String]) {
    val newTags = tags.filter(!hasTag(_))
    batchInsert((con: Connection) => {
        val ps: PreparedStatement = con.prepareStatement(_putTags)
        for (e <- newTags) {
          ps.setInt(1, e.hashCode)
          ps.setString(2, e)
          ps.addBatch
        }
        ps
      },null, null)

    batchInsert((con: Connection) => {
        val ps: PreparedStatement = con.prepareStatement(_putTagR)
        for (e <- tags) {
          ps.setInt(1, e.hashCode)
          ps.setLong(2, eventId)
          ps.addBatch
        }
        ps
      }, null, null)
  }

  def hasException(vo: ExceptionVO): Boolean =
    queryObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `exception` WHERE id =?")
        ps.setLong(1, vo.id)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

  def hasProperty(key: String): Boolean =
    queryObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `property` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

  def hasTag(key: String): Boolean =
    queryObject((con: Connection) => {
        val ps = con.prepareStatement("SELECT count(1) FROM `tag` WHERE id=?")
        ps.setInt(1, key.hashCode)
        ps
      }, RowMapping.BOOLEAN_ROW_MAPPING)

}
