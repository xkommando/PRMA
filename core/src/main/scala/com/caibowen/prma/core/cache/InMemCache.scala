package com.caibowen.prma.core.cache

import com.caibowen.prma.api.SimpleCache

import scala.collection.concurrent.TrieMap

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class InMemCache[K,V] extends SimpleCache[K,V] {

  val mem = new TrieMap[K,V]()

  def contains(key: K): Boolean = mem.contains(key)

  def clear(): Unit = mem.clear()

  def keys: Set[K] = mem.keys.asInstanceOf[Set[K]]

  def count = mem.size

  def put(key: K, value: V): Unit = mem put (key, value)

  def putIfAbsent(key: K, value: V): Unit = mem putIfAbsent(key, value)

  def putAll(map: Map[K, V]): Unit = mem ++= map// vals.foreach((e:(K,V))=>mem.put(e._1, e._2))

  def get(key: K): Option[V] = mem.get(key)

  def doUpdate(key: K, value: V, retrieve: Boolean): Option[V] = update(key, value, retrieve)

  override def update(key: K, value: V, retrieve: Boolean): Option[V] = {
    val op = mem.put(key, value)
    if (op.isDefined && retrieve)
      op
    else
      None
  }

  def toMap(): Map[K, V] = {
    val sp = mem.readOnlySnapshot()
    val mb = Map.newBuilder[K,V]
    mb.sizeHint(sp.size)
    mb ++= sp
    mb.result()
  }

  def remove(key: K, returnVal: Boolean): Option[V] = {
    if (returnVal)
      mem remove key
    else {
      mem remove key
      None
    }
  }

  def removeAll(keys: Iterable[K]): Unit = keys.foreach(mem.remove)

}
