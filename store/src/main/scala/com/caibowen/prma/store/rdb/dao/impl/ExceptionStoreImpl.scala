package com.caibowen.prma.store.rdb.dao.impl

import java.sql.{Connection, PreparedStatement}
import javax.annotation.{Nonnull, Nullable}

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcSupport, StatementCreator}
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.StrLoader
import com.caibowen.prma.store.rdb.dao.KVStore

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class ExceptionStoreImpl(private[this] val loader: StrLoader) extends JdbcSupport with KVStore[Long, ExceptionVO] {

  override def hasKey(hash: Long): Boolean = {
    queryForObject(new StatementCreator() {
      override def createStatement(con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT count(1) FROM `exception` WHERE id =?")
        ps.setLong(1, hash)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }

  override def putIfAbsent(@Nonnull values: Map[Long, ExceptionVO]): Boolean = {
    putAll(values.filterKeys(!hasKey(_)))
  }

  def putAll(@Nonnull map: Map[Long, ExceptionVO]): Boolean

}
