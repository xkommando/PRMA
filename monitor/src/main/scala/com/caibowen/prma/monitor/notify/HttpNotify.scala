package com.caibowen.prma.monitor.notify

import gplume.scala.Http
import com.caibowen.prma.monitor.eval.{Response, WithData}


//case class EvalAwareResponse extends NotifyOne

/**
 * @author BowenCai
 * @since  09/12/2014.
 */
class HttpNotify(url: String,
                 userName: String,
                 password: String) extends Notifier {

  def take(r: Response): Unit = {
    val req = r match {
      case e: WithData[_] => Http.apply(url)
          .headers(Seq("userName" -> userName,
          "password" -> password,
          "message" -> e.data.toString))
      case _ =>  Http.apply(url)
        .headers(Seq("userName" -> userName,
        "password" -> password))
    }
    req.param("data", r.event.toString)

    if (req.asBytes.code != 200)
      println("Post failed " + r.event.toString)
  }
}
