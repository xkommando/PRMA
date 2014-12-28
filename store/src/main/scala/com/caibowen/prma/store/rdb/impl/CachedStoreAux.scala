package com.caibowen.prma.store.rdb.impl

import java.sql.Connection

import com.caibowen.gplume.jdbc.mapper.RowMapping
import gplume.scala.conversion.CommonConversions._
import com.caibowen.prma.api.SimpleCache
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.{LifeCycle, StrLoader}

import scala.collection.JavaConversions._
import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class CachedStoreAux (private[this] val sqls: StrLoader)
          extends StoreAuxImpl(sqls) with LifeCycle {

  private[this] val NA = new AnyRef

  @BeanProperty var exceptCache: SimpleCache[Long, AnyRef] = _
  @BeanProperty var exceptCacheSize: Int = 128

  @BeanProperty var propCache: SimpleCache[Int, AnyRef] = _
  @BeanProperty var propsCacheSize: Int = 128

  @BeanProperty var tagCache: SimpleCache[Int, AnyRef] = _
  @BeanProperty var tagCacheSize: Int = 128

  override def start(): Unit = {
    loadInt4Keys("Tag.getIDs", tagCacheSize, tagCache)
    loadInt4Keys("Property.getPropertyIDs", propsCacheSize, propCache)
    loadInt8Keys("Exception.getExceptIDs", exceptCacheSize, exceptCache)
    super.start()
  }

  override def stop(): Unit ={
    tagCache.clear()
    propCache.clear()
    exceptCache.clear()
    super.stop()
  }

  private final def loadInt4Keys(sqlID: String, limit: Int, cache: SimpleCache[Int,AnyRef]): Unit = {
    val recentIDs = queryList((con: Connection)=> {
      val ps = con.prepareStatement(sqls.get(sqlID))
      ps.setInt(1, limit)
      ps
    }, RowMapping.INT_ROW_MAPPING)
    for (i <- recentIDs)
      cache.put(i, NA)
  }
  private final def loadInt8Keys(sqlID: String, limit: Int, cache: SimpleCache[Long,AnyRef]): Unit = {
    val recentIDs = queryList((con: Connection)=> {
      val ps = con.prepareStatement(sqls.get(sqlID))
      ps.setInt(1, limit)
      ps
    }, RowMapping.LONG_ROW_MAPPING)
    for (i <- recentIDs)
      cache.put(i, NA)
  }

  override def hasException(vo: ExceptionVO): Boolean = {
    if (exceptCache.get(vo.id).isDefined)
      true
    else {
      if (super.hasException(vo)){
        exceptCache.put(vo.id, NA)
        true
      }
      else false
    }
  }

  override def hasProperty(key: String): Boolean = {
    if (propCache.get(key.hashCode).isDefined)
      true
    else {
      if (super.hasProperty(key)){
        exceptCache.put(key.hashCode, NA)
        true
      }
      else false
    }
  }

  override def hasTag(key: String): Boolean = {
    if (tagCache.get(key.hashCode).isDefined)
      true
    else {
      if (super.hasTag(key)){
        exceptCache.put(key.hashCode, NA)
        true
      }
      else false
    }
  }
}
