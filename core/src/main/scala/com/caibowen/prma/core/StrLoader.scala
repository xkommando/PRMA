package com.caibowen.prma.core

import java.io.InputStream
import java.util.Properties

import com.caibowen.gplume.context.{InputStreamCallback, InputStreamProviderProxy, InputStreamSupport}
import com.caibowen.gplume.misc.Str.Utils._

import scala.collection.JavaConversions._

/**
 * @author BowenCai
 * @since  01/12/2014.
 */
class StrLoader(private[this] val paths: java.util.List[String]) extends InputStreamSupport with LifeCycle{

  private[this] val map = new scala.collection.mutable.HashMap[String, String]

  override def start(): Unit = {

    if (paths == null || paths.size < 1)
      throw new IllegalArgumentException("Empty paths")
    for (p <- paths if isBlank(p))
      throw new IllegalArgumentException("Empty path in " + paths)

    if (getStreamProvider == null)
      setStreamProvider(InputStreamProviderProxy.DEFAULT_PROXY)

    val props = new Properties()
    for(p <- paths) {
      withPath(p, new InputStreamCallback {
        override def doInStream(stream: InputStream): Unit = {
          if (p.endsWith(".xml"))
            props.loadFromXML(stream)
          else props.load(stream)
          for ((k, v) <- props if map.put(k, v).isDefined)
            throw new IllegalArgumentException(s"Duplicated key[$k]value[$v]")
        }
      })
      props.clear()
    }
    paths.clear()
    started = true
  }

  def keys = map.keys
  def get(k: String) = map(k)
  def put(k: String, v: String) = map.put(k, v)
  def remove(k:String) = map.remove(k)

  override def stop(): Unit = {
    map.clear()
    started = false
  }

}
