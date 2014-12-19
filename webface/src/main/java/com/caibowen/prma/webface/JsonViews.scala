package com.caibowen.prma.webface

import com.caibowen.gplume.web.{IViewResolver, RequestContext}

/**
 * @author BowenCai
 * @since  18/12/2014.
 */
class JsonResult[T](val data: AnyRef, val code: Int = 200, var message: Option[String] = None)

  extends Serializable {

  def ok = code == 200

}

class JsonViewResolver extends IViewResolver {

  override def fitness (klass: Class[_]): Int = klass match {
    case _: JsonResult[_] => 1
    case _ => -1
  }

  override def resolve(ctx: RequestContext, view: Any): Unit = {
    val jr = view.asInstanceOf[JsonResult[_]]

  }

}