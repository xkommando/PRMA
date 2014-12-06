package com.caibowen.prma.core.cache

import com.caibowen.gplume.cache.mem.Int4LIRSCache
import com.caibowen.prma.api.SimpleCache

import scala.collection.JavaConversions

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class LIRSCache[K,V] extends SimpleCache[K,V]{

  val mem = new Int4LIRSCache[V](256)

  def contains(key: K): Boolean = mem.containsKey(key.hashCode())

  def clear(): Unit = mem.clear()

  def keys(): Set[K] = throw new NotImplementedError()

  def put(key: K, value: V): Unit = {mem.put(key.hashCode(), value)}

  def putIfAbsent(key: K, value: V): Unit = {
    if (!contains(key))
      mem.put(key.hashCode(), value)
  }

  def putAll(vals: Map[K, V]): Unit = {
    val iv = vals.map((e:(K,V)) => (e._1.hashCode(), e._2))
    val mp = JavaConversions.mapAsJavaMap(iv)
    mem.putAll(mp.asInstanceOf[java.util.Map[Integer, V]])
  }

  def get(key: K): Option[V] = {
    val r = mem.get(key.hashCode())
    if (r != null)
      Some(r)
    else
      None
  }

  def doUpdate(key: K, value: V, retrieve: Boolean): Option[V] = update(key, value, retrieve)

  override def update(key: K, value: V, retrieve: Boolean): Option[V] = {
    val old = mem.put(key.hashCode(), value)
    if (old != null && retrieve)
      Some(old)
    else
      None
  }

  def toMap(): Map[K, V] = {
    val sp = JavaConversions.mapAsScalaMap(mem.getMap)
    val mb = Map.newBuilder[K,V]
    mb.sizeHint(sp.size)
    mb ++= sp.asInstanceOf[TraversableOnce[(K,V)]]
    mb.result()
  }

  def remove(key: K, returnVal: Boolean): Option[V] = {
    if (returnVal)
      Some( mem remove key.hashCode())
    else {
      mem remove key.hashCode()
      None
    }
  }

  def removeAll(keys: Iterable[K]): Unit = keys.foreach(mem remove _.hashCode())

}
