package com.caibowen.prma.webface

import java.io.PrintWriter

import com.caibowen.gplume.web.{IViewResolver, RequestContext}
import com.caibowen.prma.api.model.EventVO
import net.liftweb.json.{DefaultFormats, Serialization}

/**
 * @author BowenCai
 * @since  18/12/2014.
 */
object JsonResult {
  type Action = PrintWriter=>Unit

  val ok = new JsonResult(200, "OK")
  val invalidParameter = new JsonResult(400, "Invalid Parameter")
  val notFound = new JsonResult(404, "Not Found")
}
case class JsonResult[T](data: Option[T], code: Int, message: Option[String]) extends Serializable {

  def this(code: Int, msg: String) {
    this(None, code, Some(msg))
  }

  def this(d: T) {
    this(Some(d), 200, None)
  }

  def ok = code == 200

}
class FastJsonViewResolver extends IViewResolver {

  override def fitness (klass: Class[_]) = if (klass eq classOf[JsonResult[_]]) 1 else -1

  override def resolve(ctx: RequestContext, view: Any): Unit = view match {
    case jsr: JsonResult[_] =>
      ctx.response.setContentType("application/json")
      val w = ctx.response.getWriter
      w.append("{\r\n\"code\":").write(jsr.code.toString)
      if (jsr.message.isDefined)
        w.append(",\r\n\"message\":\"").append(jsr.message.get).append('\"')
      if (jsr.data.isDefined) {
        jsr.data.get match {
          case evls: Seq[EventVO] =>
            w.append(",\r\n\"data\":[")
            //          evls.take(evls.size - 1).foreach(ev => w.append(ev.appendJson(new StringBuilder(2048)).toString).append('\n').append(','))
            evls.take(evls.size - 1).foreach(ev => w.append(ev.toString).append('\n').append(','))
            w.append(evls.last.toString).append(']')

          case ev: EventVO =>
            w.append(",\r\n\"data\":")
              .append(ev.toString)
          //          .append(ev.get.appendJson(new StringBuilder(2048)).toString))

          case action: JsonResult.Action =>
            action(w)
          case None =>
          case obj =>
            w.append(",\r\n\"data\":")
            Serialization.writePretty(obj.asInstanceOf[AnyRef], w)(DefaultFormats)
//            Serialization.write(obj.asInstanceOf[AnyRef], w)(DefaultFormats)
        }
      }
      w.append("\r\n}")
      w.flush()
  }

}