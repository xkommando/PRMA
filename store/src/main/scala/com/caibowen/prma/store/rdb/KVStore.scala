package com.caibowen.prma.store.rdb

import javax.annotation.{Nonnull, Nullable}

import com.caibowen.gplume.jdbc.JdbcException

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
  def get(key: K): Option[V]

  protected
  def doPut(key: K, @Nonnull value: V): Boolean

  def put(key: K, @Nonnull value: V): Boolean =
    if(hasKey(key))
      update(key, value)
    else
      doPut(key, value)


  def putIfAbsent(key: K, @Nonnull value: V): Boolean =
    if (hasKey(key))
      false
    else
      doPut(key, value)

  def putIfAbsent(@Nonnull ls: List[(K, V)]): Unit =
    putAll(ls.filter(tp=> !hasKey(tp._1)))


  def putIfAbsent(@Nonnull values: Map[K, V]): Unit =
    putAll(values.filterKeys(!hasKey(_)))


  def putAll(@Nonnull ls: List[(K,V)])
  def putAll(@Nonnull map: Map[K, V])

  def update(key: K, @Nonnull value: V): Boolean

  @Nullable
  def remove(key: K, returnVal: Boolean): Option[V] = {
    val ret = if (returnVal) Some(get(key)) else None
    val ok = doRemove(key)
    if (!ok) throw new JdbcException(s"failed from delete value, id=$key")
    ret.asInstanceOf[Option[V]]
  }

  @Nullable
  def doRemove(key: K): Boolean
}
