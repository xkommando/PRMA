package com.caibowen.prma.core

import com.caibowen.prma.api.LogLevel
import net.liftweb.json.CustomSerializer
import net.liftweb.json.JsonAST.JString

/**
 * @author BowenCai
 * @since  15/12/2014.
 */
case object LogLevelSerivializer extends CustomSerializer[LogLevel.LogLevel](format => (
  {
    case JString(s) =>  LogLevel.from(s)
  },
  {
    case d: LogLevel.LogLevel => JString(d.toString)
  }
  ))

//case object StaclTraceSerivializer extends CustomSerializer[StackTraceElement](format => (
//  {
//    case node: JObject =>{
//      val ls = node.children
//      val file = ls(0).values.asInstanceOf[(String, String)]._2
//      val cls = ls(0).values.asInstanceOf[(String, String)]._2
//    }
//  },
//  {
//    case d: LogLevel.LogLevel => JString(d.toString)
//  }
//  ))

object JSON {

}
