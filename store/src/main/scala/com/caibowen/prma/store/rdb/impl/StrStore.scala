package com.caibowen.prma.store.rdb.impl

import java.sql.Connection
import javax.annotation.Nonnull

import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.jdbc.mapper.RowMapping
import com.caibowen.gplume.scala.conversion.CommonConversions._
import com.caibowen.prma.store.rdb.KVStore

@SerialVersionUID(5288491677679354232L)
class StrStore(private[this] val tableName: String) extends JdbcSupport with KVStore[Int, String] {

  override def hasKey(key: Int): Boolean =
    queryObject((con: Connection) => {
      val ps = con.prepareStatement("SELECT count(1) FROM " + tableName + " WHERE id=?")
      ps.setInt(1, key)
      ps
    }, RowMapping.BOOLEAN_ROW_MAPPING)

  override def hasVal(@Nonnull value: String): Boolean =
    queryObject((con: Connection) => {
      val ps = con.prepareStatement("SELECT COUNT (1) FROM " + tableName + " WHERE `value` = ? ")
      ps.setString(1, value)
      ps
    }, RowMapping.BOOLEAN_ROW_MAPPING)

  override def get(key: Int): Option[String] = {
    val r = queryObject((con: Connection) => {
      val ps = con.prepareStatement("SELECT `value` FROM " + tableName + " WHERE id=? LIMIT 1")
      ps.setInt(1, key)
      ps
    }, RowMapping.STR_ROW_MAPPING)
    if (r == null)
      None
    else Some(r)
  }

  override def update(key: Int, @Nonnull value: String): Boolean =
    execute((con: Connection) => {
      val ps = con.prepareStatement("UPDATE " + tableName + " SET `value` = ? where id=" +
        key)
      ps.setString(1, value)
      ps
    })

  protected def doPut(key: Int, @Nonnull value: String): Boolean = {
    insert((con: Connection) => {
      val ps = con.prepareStatement("INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)")
      ps.setInt(1, key)
      ps.setString(2, value)
      ps
    }, null, null)
    true
  }

  override def putAll(@Nonnull ls: List[(Int, String)]): Unit =
    if (ls.length > 0)
    batchInsert((con: Connection) => {
      val ps = con.prepareStatement("INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)")
      for (e <- ls) {
        ps.setInt(1, e._1)
        ps.setString(2, e._2)
        ps.addBatch()
      }
      ps
    }, null, null)

  override def putAll(@Nonnull map: Map[Int, String]): Unit =
    if (map.size > 0)
    batchInsert((con: Connection) => {
      val ps = con.prepareStatement("INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)")
      for ((key, value) <- map) {
        ps.setInt(1, key)
        ps.setString(2, value)
        ps.addBatch()
      }
      ps
    }, null, null)

  override def doRemove(key: Int): Boolean = {
    execute((con: Connection) => {
      con.prepareStatement("DELETE FROM " + tableName + "where id=" + key)
    })
    true
  }
}
