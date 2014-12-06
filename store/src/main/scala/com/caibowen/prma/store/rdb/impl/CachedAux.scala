package com.caibowen.prma.store.rdb.impl

import com.caibowen.prma.api.SimpleCache
import com.caibowen.prma.api.model.ExceptionVO
import com.caibowen.prma.core.{StrLoader, LifeCycle}

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  06/12/2014.
 */
class CachedAux private[this](val sqls: StrLoader)
          extends StoreAuxImpl(sqls) with LifeCycle {

  private[this] val NA = new AnyRef

  @BeanProperty var exceptCache: SimpleCache[Long, AnyRef] = _
  @BeanProperty var propCache: SimpleCache[Long, AnyRef] = _
  @BeanProperty var markerCache: SimpleCache[Long, AnyRef] = _


  override def start(): Unit = {

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

  override def hasMarker(key: String): Boolean = {
    if (markerCache.get(key.hashCode).isDefined)
      true
    else {
      if (super.hasProperty(key)){
        exceptCache.put(key.hashCode, NA)
        true
      }
      else false
    }
  }
}
