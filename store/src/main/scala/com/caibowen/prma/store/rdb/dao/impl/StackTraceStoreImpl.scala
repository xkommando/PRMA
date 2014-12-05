package com.caibowen.prma.store.rdb.dao.impl

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}
import javax.annotation.Nonnull

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcSupport, StatementCreator}
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.rdb.dao.KVStore

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
object StackTraceStore {
  val ST_MAPPING = new RowMapping[StackTraceElement]() {
    override def extract(@Nonnull rs: ResultSet): StackTraceElement = {
      val file = rs.getString(1)
      val klass = rs.getString(2)
      val func = rs.getString(3)
      val line = rs.getInt(4)
      new StackTraceElement(klass, func, file, line)
    }
  }
}
class StackTraceStore(private[this] val loader: StrLoader) extends JdbcSupport with KVStore[Int,StackTraceElement] {

  override def hasKey(key: Int): Boolean = {
    0 < queryForObject(new StatementCreator() {
        @Nonnull
        override def createStatement(@Nonnull con: Connection): PreparedStatement = {
          val ps = con.prepareStatement("SELECT count(1) FROM `stack_trace` WHERE id=?")
          ps.setInt(1, key)
          ps
        }
      }, RowMapping.INT_ROW_MAPPING)
  }

  override def hasVal(@Nonnull `val`: StackTraceElement): Boolean = hasKey(`val`.hashCode)

  override def get(key: Int): StackTraceElement = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT `file`,`class`,`function`,`line` FROM `stack_trace` WHERE id=?")
        ps.setInt(1, key)
        ps
      }
    }, StackTraceStore.ST_MAPPING)
  }


  protected def doPut(key: Int, @Nonnull value: StackTraceElement): Boolean = {
    insert(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("INSERT INTO `stack_trace`(`id`,`file`,`class`,`function`,`line`)VALUES(?,?,?,?,?)")
        ps.setLong(1, value.hashCode)
        ps.setString(2, value.getFileName)
        ps.setString(3, value.getClassName)
        ps.setString(4, value.getMethodName)
        ps.setInt(5, value.getLineNumber)
        return ps
      }
    }, null, null)
    true
  }

  override def putAll(@Nonnull ls: List[StackTraceElement]): Boolean = {
    batchInsert(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("INSERT INTO `stack_trace`(`id`,`file`,`class`,`function`,`line`)VALUES(?,?,?,?)")
        for (value <- ls) {
          ps.setLong(1, value.hashCode)
          ps.setString(2, value.getFileName)
          ps.setString(3, value.getClassName)
          ps.setString(4, value.getMethodName)
          ps.setInt(5, value.getLineNumber)
          ps.addBatch()
        }
        return ps
      }
    }, null, null)
    true
  }

  @Nonnull
  override def update(key: Int, @Nonnull value: StackTraceElement): Boolean = {
    execute(new StatementCreator() {

      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("UPDATE `stack_trace` SET `file` = ?,`class`=?,`function`=?,`line`=? where id=?")
        ps.setString(1, value.getFileName)
        ps.setString(2, value.getClassName)
        ps.setString(3, value.getMethodName)
        ps.setInt(4, value.getLineNumber)
        ps.setLong(5, key)
        ps
      }
    })
  }

  override def remove(key: Int, returnVal: Boolean): StackTraceElement = {

    val ret = if (returnVal) get(key) else null
    val ok = execute(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        return con.prepareStatement("DELETE FROM `stack_trace`where id=" + key)
      }
    })

    if (!ok) {
      val msg = if (ret == null) "not retrieved" else ret.toString
      throw new SQLException(
        s"failed from delete stack trace, id=[$key] stacktrace[$msg]")
    }
    ret
  }
}
