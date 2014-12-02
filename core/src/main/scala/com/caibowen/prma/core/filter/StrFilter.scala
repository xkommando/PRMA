package com.caibowen.prma.core.filter

import java.io.{BufferedReader, InputStream, InputStreamReader}

import com.caibowen.gplume.context.{InputStreamCallback, InputStreamProviderProxy, InputStreamSupport}
import com.caibowen.gplume.misc.Str.Utils
import com.caibowen.gplume.misc.Str.Utils._

import scala.collection.mutable

/**
* @author BowenCai
* @since  01/12/2014.
*/
class StrFilter(configPath: String) extends InputStreamSupport with Filter[String] {

  protected[this] val matchFull = new mutable.HashSet[String]


  @inline
  protected def parse(ptn: String): Unit = {
    matchFull.add(ptn)
  }

  override def start(): Unit = {
    if (started)
      return

    if (isBlank(configPath)) {
      started = true
      return
    }

    if (getStreamProvider == null)
      setStreamProvider(InputStreamProviderProxy.DEFAULT_PROXY)
    withPath(configPath, new InputStreamCallback {
      override def doInStream(stream: InputStream): Unit = {
        val reader = new BufferedReader(new InputStreamReader(stream))
        // how to go FP???
        var buf = reader.readLine();
        while (null != buf) {
          if (notBlank(buf))
            parse(buf);
            buf = reader.readLine();
        }
        reader.close();
      }
    });

    started = true
  }

  @inline
  override def doAccept(str: String): Int = {
    if (matchFull.contains(str))
      1
    else
      -1
  }

  override def stop(): Unit = {
    matchFull.clear()
  }


}
