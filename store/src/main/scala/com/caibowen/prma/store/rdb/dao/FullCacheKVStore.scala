package com.caibowen.prma.store.rdb.dao

import com.caibowen.prma.core.LifeCycle

/**
 * @author BowenCai
 * @since  05/12/2014.
 */
class FullCacheKVStore[K,V](private[this] db: KVStore[K,V]) extends KVStore[K,V] with LifeCycle {

  override def start(): Unit = {

  }

  override def stop(): Unit = {
    started = false
  }
}
