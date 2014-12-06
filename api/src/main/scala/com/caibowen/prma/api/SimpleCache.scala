package com.caibowen.prma.api

import scala.concurrent.Future

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
trait SimpleCache[K,V] {

  def contains(key: K): Boolean

  def clear(): Unit

  def keys(): Set[K]

  /**
   * put value.
   * behavior undefined if there is old value
   * @param key
   * @param value
   */
  def put(key: K, value: V): Unit

  /**
   * put only if this key is not contained
   * @param key
   * @param value
   */
  def putIfAbsent(key: K, value: V): Unit

  /**
   * put value.
   * behavior undefined if there is old value
   * @param vals
   */
  def putAll(vals: Map[K, V]): Unit

  /**
   * update, return old value if required
   * behavior undefined if there is no old value
   *
   * @param key
   * @param value
   * @param retrieve
   * @return old value
   */
  def doUpdate(key: K, value: V, retrieve: Boolean): Option[V]

  /**
   * update if old value exists, return old value if requested
   * or put new value and return None
   *
   * @param key
   * @param value
   * @param retrieve
   * @return old value, if exists and requested
   */
  def update(key: K, value: V, retrieve: Boolean): Option[V] = {
    var op = get(key)
    if (op isDefined) {
      op = doUpdate(key, value, retrieve)
      if (op.isDefined && retrieve)
        op
      else None
    } else {
      put(key, value)
      None
    }
  }

  def get(key: K): Option[V]

  def apply(key: K): V = {
    val op = get(key)
    if (op isDefined)
      op.get
    else
      null.asInstanceOf[V]
  }

  /**
   * @return a map view
   */
  def toMap(): Map[K, V]

  /**
   * @param key
   * @param returnVal
   * @return old value, if exists and requested
   */
  def remove(key: K, returnVal: Boolean): Option[V]

  def removeAll(keys: Iterable[K]): Unit

  import scala.concurrent.ExecutionContext.Implicits.global

  def containsAsync(key: K): Future[Boolean] = Future {
    contains(key)
  }


  def clearAsync(): Future[Unit] = Future {
    clear
  }

  def keysAsync(): Future[Set[K]] = Future {
    keys
  }

  def putAsync(key: K, value: V): Future[Unit] = Future {
    put(key, value)
  }

  def putIfAbsentAsync(key: K, value: V): Future[Unit] = Future{
    putIfAbsent(key, value)
  }

  def putAllAsync(vals: Map[K, V]): Future[Unit] = Future{
    putAll(vals)
  }

  def getAsync(key: K): Future[Option[V]] = Future {
    get(key)
  }

  def removeAsync(key: K, returnVal: Boolean): Future[Option[V]] = Future{
    remove(key, returnVal)
  }

  def toMapAsync(): Future[Map[K, V]] = Future {
    this.toMap()
  }

  def removeAllAsync(keys: Iterable[K]): Future[Unit] = Future {
    removeAll(keys)
  }

  def updateAsync(key: K, value: V, retrieve: Boolean): Future[Option[V]] = Future {
    update(key, value, retrieve)
  }

}
