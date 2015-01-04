package com.caibowen.prma.webface

import com.caibowen.gplume.web.{IViewResolver, RequestContext}
import com.caibowen.prma.api.model.{ExceptionVO, EventVO}

/**
 * @author BowenCai
 * @since  18/12/2014.
 */
class JsonResult[T](val data: T, val code: Int, var message: Option[String])

  extends Serializable {

  def this(d: T) {
    this(d, 200, None)
  }
  def this(d: T, msg: String) {
    this(d, 200, Some(msg))
  }
  def ok = code == 200

}

class JsonViewResolver extends IViewResolver {

  override def fitness (klass: Class[_]) = if (klass eq classOf[JsonResult[_]]) 1 else -1

  override def resolve(ctx: RequestContext, view: Any): Unit = view match {
    case jsr: JsonResult[_] =>
      ctx.response.setContentType("application/json")
      val w = ctx.response.getWriter
      w.append("{\r\n\"code\":").write(jsr.code)
      if (jsr.message.isDefined)
        w.append(",\r\n\"message\":\"").append(jsr.message.get).append('\"')

      w.append(",\r\n\"data\":")

      jsr.data match {
        case evls: List[EventVO] =>
          w.append('[')
          evls.take(evls.size - 1).foreach(ev => w.append(ev.toString).append('\n').append(','))
          w.append(evls.last.toString).append(']')

        case ev: EventVO =>
          w.append(ev.toString)

        case except: ExceptionVO =>
          w.append(except.toString)
      }

      w.append("\r\n}")
      w.flush()
  }

}