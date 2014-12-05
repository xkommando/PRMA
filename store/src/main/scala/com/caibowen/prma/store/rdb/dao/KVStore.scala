package com.caibowen.prma.store.rdb.dao

import javax.annotation.{Nonnull, Nullable}

import com.caibowen.gplume.jdbc.JdbcException
import com.caibowen.prma.core.LifeCycle

/**
 * level, loggerName, threadName, message
 * callerStackTrace
 *
 *
 * @author BowenCai
 * @since 22-10-2014.
 */
trait KVStore[K,V] extends Serializable {

  def hasKey(key: K): Boolean

  def hasVal(@Nonnull value: V): Boolean

  @Nullable
  def get(key: K): V

  protected
  def doPut(key: K, @Nonnull value: V): Boolean

  def put(key: K, @Nonnull value: V): Boolean = {
    if(hasKey(key))
      update(key, value)
    else
      doPut(key, value)
  }

  def putIfAbsent(key: K, @Nonnull value: V): Boolean = {
    if (hasKey(key))
      false
    else
      doPut(key, value)
  }

  def putIfAbsent(@Nonnull values: Map[K, V]): Boolean = {
    putAll(values.filterKeys(!hasKey(_)))
  }

  def putAll(@Nonnull map: Map[K, V]): Boolean

  def update(key: K, @Nonnull value: V): Boolean

  @Nullable
  def remove(key: K, returnVal: Boolean): V = {
    val ret = if (returnVal) get(key) else null
    val ok = doRemove(key)
    if (!ok) throw new JdbcException(s"failed from delete value, id=$key")
    ret
  }

  @Nullable
  def doRemove(key: K): Boolean
}
