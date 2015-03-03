package com.caibowen.prma.webface

import java.io.PrintWriter

import com.caibowen.gplume.web.{IViewResolver, RequestContext}
import com.caibowen.prma.api.model.{Helper, EventVO}
import com.caibowen.prma.core.{JSON, ExceptVOSerializer, EventVOSerializer}
import net.liftweb.json.{DefaultFormats, Serialization}

/**
 * @author BowenCai
 * @since  18/12/2014.
 */
object JsonResult {
  type Action = (PrintWriter)=>Unit

  val ok = new JsonResult(200, "OK")
  val invalidParameter = new JsonResult(400, "Invalid Parameter")
  val notFound = new JsonResult(404, "Not Found")
  object NOP extends JsonResult
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
class JsonViewResolver extends IViewResolver {

  override def fitness (klass: Class[_]) = if (klass eq classOf[JsonResult[_]]) 1 else -1


  override def resolve(ctx: RequestContext, view: Any): Unit = view match {
    case JsonResult.NOP =>
    case jsr: JsonResult[_] =>
      ctx.response.setContentType("application/json")
      val w = ctx.response.getWriter
      w.append("{\r\n\"code\":").write(jsr.code.toString)
      if (jsr.message.isDefined)
        w.append(",\r\n\"message\":\"")
          .append(Helper.appendQuote(jsr.message.get)(new java.lang.StringBuilder(64)).toString)
          .append('\"')

      w.append(",\r\n\"data\":")
      if (jsr.data.isDefined) {
//        jsr.data.get match {
//          case action: JsonResult.Action =>
//            action(w)
//          case obj =>
//            Serialization.writePretty(obj.asInstanceOf[AnyRef], w)(fmt)
//        }
        Serialization.writePretty(jsr.data.get.asInstanceOf[AnyRef], w)(JSON.fmt)
      } else {
        w.append("{}")
      }

      w.append("}")
      w.flush()
  }

}