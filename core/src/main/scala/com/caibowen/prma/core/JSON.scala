package com.caibowen.prma.core

import com.caibowen.prma.api.LogLevel
import com.caibowen.prma.api.model.{Helper, EventVO, ExceptionVO}
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.{parse, _}

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

object JSON {

  val fmt = (DefaultFormats + EventVOSerializer) + ExceptVOSerializer

  def write(vo: EventVO): String = vo.toString
  def write(vo: ExceptionVO): String = vo.toString

  def readEventVO(js: String): EventVO = {
    val ast = parse(js)
    implicit val ls = ast.children
    val id = extractLong(0)
    val time = extractLong(1)
    val level = LogLevel.from(extractStr(2))
    val logger = extractStr(3)
    val thread = extractStr(4)
    val flag = extractLong(5)
    val message = extractStr(6)
    val caller = extractST(ls(7).asInstanceOf[JField].value.asInstanceOf[JObject])

    val revN = ast \ "reserved"
    val exceptN = ast \ "exceptions"
    val tagN = ast \ "tags"
    val propN = ast \ "properties"
    val reserved = if (revN == JNothing) None
    else
      Some(revN.asInstanceOf[JInt].num.toLong)

    val tags = if (tagN == JNothing || tagN.children.size == 0) None
    else
      Some(tagN.children.map(_.asInstanceOf[JString].values).toSet)

    val props = if (propN == JNothing || propN.children.size == 0) None
    else
      Some(propN.children.map((v: JValue) => {
        val f = v.asInstanceOf[JField]
        f.name -> f.value.asInstanceOf[JString].values
      }).toMap)

    val excepts = if (exceptN == JNothing|| exceptN.children.size == 0) None
    else Some(exceptN.children.map(extractExcept).toVector)

    new EventVO(id, time, level, logger, thread, caller, flag, message, reserved, props, excepts, tags)
  }

  def readExceptionVO(js: String): ExceptionVO = {
    val ast = parse(js)
    extractExcept(ast)
  }

  private[this] def extractInt(idx: Int)(implicit ls: List[JValue]): Int = ls(idx).asInstanceOf[JField].value.asInstanceOf[JInt].num.toInt
  private[this] def extractLong(idx: Int)(implicit ls: List[JValue]): Long = ls(idx).asInstanceOf[JField].value.asInstanceOf[JInt].num.toLong
  private[this] def extractStr(idx: Int)(implicit ls: List[JValue]): String = ls(idx).asInstanceOf[JField].value.asInstanceOf[JString].values

  private[this] def extractST(jo: JObject): StackTraceElement = {
    implicit val ls = jo.obj
    new StackTraceElement(extractStr(1),
      extractStr(2),
      extractStr(0),
      extractInt(3))
  }

  private[this] def extractExcept(j: JValue): ExceptionVO = {
    val ls = j.asInstanceOf[JObject].obj
    val id = extractLong(0)(ls)
    val name = extractStr(1)(ls)
    val msgN = j \ "message"
    val msg = if (msgN == JNothing) None else Some(msgN.asInstanceOf[JString].values)
    val stsN = j \ "stackTraces"
    val sts = if (stsN == JNothing || stsN.children.size == 0) None
    else Some(
      stsN.children.map(
        (t: JValue)=>extractST(t.asInstanceOf[JObject])
      ).toVector)

    new ExceptionVO(id, name, msg, sts)
  }
}

case object EventVOSerializer extends CustomSerializer[EventVO](format => (
  {
    case JString(s) => JSON.readEventVO(s)
    case JNull => null
  },
  {
    case d: EventVO => JString(d.toString)
  }
  ))
case object ExceptVOSerializer extends CustomSerializer[ExceptionVO](format => (
  {
    case JString(s) => JSON.readExceptionVO(s)
    case JNull => null
  },
  {
    case d: ExceptionVO => JString(d.toString)
  }
  ))
