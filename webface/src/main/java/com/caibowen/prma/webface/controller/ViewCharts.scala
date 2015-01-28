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
//@Controller("/chart")
class ViewCharts {

  @BeanProperty  var database: DB = _

//  @Handle(Array("/statistics.json"))
  @Handle(Array("/testdata-charts.json"))
  def chartData(ctx: RequestContext): JsonResult[_] = {
    val minTime = ctx.getLongParam("minTime")
    val maxTime = ctx.getLongParam("maxTime")
    val lowLevel = ctx.getIntParam("lowLevel")
    val highLevel = ctx.getIntParam("highLevel")
    val interval = ctx.getLongParam("interval")
    if (minTime == null || maxTime == null ||
      lowLevel == null || highLevel == null || lowLevel >= highLevel ||
      interval == null || interval > (maxTime - minTime))
      JsonResult.invalidParameter
    else {
//      new JsonResult(
//        database.readOnlySession{implicit session=>
//          Statistician.timelineCounter(minTime, maxTime, lowLevel, highLevel, interval))

      ctx.renderAsStatic()
      JsonResult.NOP
    }

//    })
  }
}
