package com.caibowen.prma.webface.controller

import com.caibowen.gplume.web.RequestContext
import com.caibowen.gplume.web.annotation.{Handle, Controller}
import com.caibowen.prma.query.Statistician
import com.caibowen.prma.webface.JsonResult
import com.caibowen.prma.webface.JsonResult.Action
import gplume.scala.jdbc.DB

import scala.beans.BeanProperty


/**
 * Created by Bowen Cai on 1/11/2015.
 */
@Controller("/chart")
class ViewCharts {

  @BeanProperty  var database: DB = _

  @Handle(Array("/statistics"))
  def chartData(ctx: RequestContext): JsonResult[_] = {
    val minTime = ctx.getLongParam("minTime")
    val maxTime = ctx.getLongParam("maxTime")
    if (minTime == null || maxTime == null)
      JsonResult.invalidParameter
    else new JsonResult(
      database.readOnlySession{implicit session=>
      Statistician.timelineCounter(0, System.currentTimeMillis())
    })
  }
}
