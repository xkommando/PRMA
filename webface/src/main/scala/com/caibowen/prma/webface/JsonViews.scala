package com.caibowen.prma.webface

import com.caibowen.gplume.web.{IViewResolver, RequestContext}

/**
 * @author BowenCai
 * @since  18/12/2014.
 */
class JsonResult[T](val data: T, val code: Int, var message: Option[String])

  extends Serializable {

  def this(d: T) {
    this(d, 200, None)
  }

  def ok = code == 200

}

class JsonViewResolver extends IViewResolver {

  override def fitness (klass: Class[_]): Int = klass match {
    case a : JsonResult[_] => 1
    case _ => -1
  }

  override def resolve(ctx: RequestContext, view: Any): Unit = view match {
    case jsr: JsonResult[_] =>
  }

}