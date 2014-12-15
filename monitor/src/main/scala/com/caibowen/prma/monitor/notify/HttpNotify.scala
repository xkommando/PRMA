package com.caibowen.prma.monitor.notify

import com.caibowen.gplume.scala.Http
import com.caibowen.prma.api.model.EventVO
/**
 * @author BowenCai
 * @since  09/12/2014.
 */
class HttpNotify private[this](val url: String, val params: Map[String,String]) extends Notifier {

  def send(vo: EventVO): Unit = {
    val resp = Http(url).postData(vo.toString).params(params).asBytes
    if (resp.code != 200)
      println("Post failed " + vo.toString)
  }

}
