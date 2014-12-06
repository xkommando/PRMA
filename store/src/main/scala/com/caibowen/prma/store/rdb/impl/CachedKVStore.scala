package com.caibowen.prma.store.rdb.impl

import javax.annotation.{Nonnull, Nullable}

import com.caibowen.prma.api.SimpleCache
import com.caibowen.prma.core.LifeCycle
import com.caibowen.prma.store.rdb.KVStore

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class CachedKVStore[K,V]private[this](val db: KVStore[K,V], val cache: SimpleCache[K,V])
  extends KVStore[K,V] with LifeCycle {

  def hasKey(key: K): Boolean = {
    if (cache contains key)
      true
    else db hasKey key
  }

  def hasVal(@Nonnull value: V): Boolean = db.hasVal(value)

  @Nullable
  def get(key: K): Option[V] = {
    var op = cache get key
    if (op isEmpty) {
      op = db get key
      if (op isDefined)
        cache put(key, op.get)
    }
    op
  }

  protected
  def doPut(key: K, @Nonnull value: V): Boolean = {
    cache put(key, value)
    db put(key, value)
  }

  def putAll(@Nonnull ls: List[(K,V)]): Unit = {
    db putAll ls
    cache putAll ls.toMap
  }

  def putAll(@Nonnull map: Map[K, V]): Unit ={
    db putAll map
    cache putAll map
  }

  def update(key: K, @Nonnull value: V): Boolean = {
    val ok = db update(key, value)
    if (ok)
      cache.update(key, value, false)
    ok
  }

  @Nullable
  def doRemove(key: K): Boolean = {
    val ok = db doRemove(key)
    if (ok)
      cache.remove(key, false)
    ok
  }
}
