package com.caibowen.prma.core

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.{ExceptionVO, EventVO}
import net.liftweb.json.JsonAST.JString
import net.liftweb.json._

/**
 * @author BowenCai
 * @since  15/12/2014.
 */
case object LogLevelSerializer extends CustomSerializer[LogLevel.LogLevel](format => (
  {
    case JString(s) =>  LogLevel.from(s)
  },
  {
    case d: LogLevel.LogLevel => JString(d.toString)
  }
  )
)

case object StackTraceSerializer extends CustomSerializer[StackTraceElement](format => (
  {
    case JObject(List(JField("file",JString(file)),
                          JField("class",JString(_c)),
                          JField("function",JString(_f)),
                          JField("line",JInt(_l)))) => new StackTraceElement(_c, _f, file, _l.toInt)
  },
  {
    case d: StackTraceElement => JObject(List(JField("file",JString(d.getFileName)),
      JField("class",JString(d.getClassName)),
      JField("function",JString(d.getMethodName)),
      JField("line",JInt(d.getLineNumber))))
  }
  ))

object H {

  def extractInt(idx: Int)(implicit ls: List[JValue]): Int = (ls(idx).asInstanceOf[JInt]).num.toInt
  def extractLong(idx: Int)(implicit ls: List[JValue]): Long = (ls(idx).asInstanceOf[JInt]).num.toLong
  def extractStr(idx: Int)(implicit ls: List[JValue]): String = (ls(idx).asInstanceOf[JString]).values
  def extractST(j: JValue): StackTraceElement = {
    val ls = j.asInstanceOf[JObject].children
    new StackTraceElement(extractStr(1)(ls),
      extractStr(2)(ls),
      extractStr(0)(ls),
      extractInt(3)(ls))
  }

  def fromJObj(j: JValue): ExceptionVO = {
    val ls = j.asInstanceOf[JObject].children

    val id = extractLong(0)(ls)
    val name = extractStr(1)(ls)
    val msgN = j \ "message"
    val msg = if (msgN == JNothing) None else Some(msgN.asInstanceOf[JString].values)
    val stsN = j \ "stackTraces"
    val sts = if (stsN == JNothing) None else Some(stsN.children.map(extractST).toList)
    new ExceptionVO(id, name, msg, sts)
  }
}

case object ExceptVOSerializer extends CustomSerializer[ExceptionVO](format => (
  {
    case j: JObject => H.fromJObj(j)
  },
  {
    case vo: ExceptionVO => JNothing
  }
  ))

case object EventVOSerializer extends CustomSerializer[EventVO](format => (
  {
    case j: JObject => {
      implicit val ls = j.children

      val id = H.extractLong(0)
      val time = H.extractLong(1)
      val level = LogLevel.from(H.extractStr(2))
      val logger = H.extractStr(3)
      val thread = H.extractStr(4)
      val flag = H.extractLong(5)
      val message = H.extractStr(6)
      val caller = H.extractST(ls(7))

      val revN = j \ "reserved"
      val exceptN = j \ "exceptions"
      val tagN = j \ "tags"
      val  propN = j \ "properties"
      val reserved = if (revN == JNothing) None else
        Some(revN.asInstanceOf[JInt].num.toLong)

      val tags = if (tagN == JNothing) None else
        Some(tagN.children.map(_.asInstanceOf[JString].values).toSet)

      val props = if (propN == JNothing) None else
        Some(propN.children.map((v: JValue) => {
          val f = v.asInstanceOf[JField]
          f.name -> f.value.asInstanceOf[JString].values}).toMap)

      val excepts = if (exceptN == JNothing) None else Some(exceptN.children.map(H.fromJObj).toList)

      new EventVO(id, time, level, logger, thread, caller, flag, message, reserved, props, excepts, tags)
    }
  },
  {
  case vo: EventVO=> JNothing
  }
  ))

object JSON {

}
