package com.caibowen.prma.store.rdb.dao.impl

import java.sql.{Connection, PreparedStatement}
import javax.annotation.Nonnull

import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.jdbc.{JdbcSupport, StatementCreator}
import com.caibowen.prma.store.rdb.dao.KVStore

@SerialVersionUID(5288491677679354232L)
class StrStore(private[this] val tableName: String) extends JdbcSupport with KVStore[Int, String] {

  override def hasKey(key: Int): Boolean = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT count(1) FROM " + tableName + " WHERE id=?")
        ps.setInt(1, key)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }

  override def hasVal(@Nonnull value: String): Boolean = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT COUNT (1) FROM " + tableName + " WHERE `value` = ? ")
        ps.setString(1, value)
        ps
      }
    }, RowMapping.BOOLEAN_ROW_MAPPING)
  }

  override def get(key: Int): String = {
    queryForObject(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("SELECT `value` FROM " + tableName + " WHERE id=? LIMIT 1")
        ps.setInt(1, key)
        ps
      }
    }, RowMapping.STR_ROW_MAPPING)
  }

  override def update(key: Int, @Nonnull value: String): Boolean = {
    execute(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("UPDATE " + tableName + " SET `value` = ? where id=" +
          key)
        ps.setString(1, value)
        ps
      }
    })
  }

  protected def doPut(key: Int, @Nonnull value: String): Boolean = {
    insert(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)")
        ps.setInt(1, key)
        ps.setString(2, value)
        return ps
      }
    }, null, null)
    true
  }

  override def putAll(@Nonnull map: Map[Int, String]): Boolean = {
    batchInsert(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        val ps = con.prepareStatement("INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)")
        for ((key, value) <- map) {
          ps.setInt(1, key)
          ps.setString(2, value)
          ps.addBatch()
        }
        return ps
      }
    }, null, null)
    true
  }


  override def doRemove(key: Int): String = {
    execute(new StatementCreator() {
      @Nonnull
      override def createStatement(@Nonnull con: Connection): PreparedStatement = {
        return con.prepareStatement("DELETE FROM " + tableName + "where id=" + key)
      }
    })
  }
}
