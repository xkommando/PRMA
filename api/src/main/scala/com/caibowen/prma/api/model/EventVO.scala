package com.caibowen.prma.api.model

import com.caibowen.prma.api.LogLevel.LogLevel
import java.lang.{StringBuilder => JStrBuilder}
/**
* @author BowenCai
* @since  02/12/2014.
*/
@SerialVersionUID(-8179577194579626226L)
case class EventVO(id: Long,
                   timeCreated: Long,
                   level: LogLevel,
                   loggerName: String,
                   threadName: String,
                   callerStackTrace: StackTraceElement,
                   flag: Long,
                   message: String,
                   reserved: Option[Long],
                   properties: Option[Map[String, Any]],
                   exceptions: Option[Vector[ExceptionVO]],
                   tags: Option[Set[String]]) extends Serializable {

  require(loggerName != null, "Logger name cannot be null")
  require(threadName != null, "Logger name cannot be null")
  require(message != null, "message cannot be null")
  require(callerStackTrace != null, "caller stackTrace cannot be null")

  def this(timeCreated: Long,
           level: LogLevel,
           loggerName: String,
           threadName: String,
           callerStackTrace: StackTraceElement,
           message: String,
           reserved: Long,
           properties: Map[String, Any],
           exceptions: Vector[ExceptionVO],
           markers: Set[String]) {

    this(-1L, timeCreated, level, loggerName,
      threadName, callerStackTrace, EventVO.buildFlag(properties, markers, exceptions),
      message,
      if (reserved == -1) None else Some(reserved),
      if (properties == null) None else Some(properties),
      if (exceptions == null) None else Some(exceptions),
      if (markers == null) None else Some(markers)
    )
  }

  override def hashCode(): Int = {
    var result: Int = (id ^ (id >>> 32)).toInt
    result = 31 * result + (timeCreated ^ (timeCreated >>> 32)).toInt
    result = 31 * result + level.id
    result = 31 * result + loggerName.hashCode
    result = 31 * result + threadName.hashCode
    result = 31 * result + (if (callerStackTrace != null) callerStackTrace.hashCode else 0)
    result = 31 * result + (flag ^ (flag >>> 32)).toInt
    result = 31 * result + message.hashCode
    result = 31 * result + (if (reserved.isDefined) reserved.get.hashCode else 0)
    result = 31 * result + (if (properties.isDefined) properties.get.hashCode() else 0)
    result = 31 * result + (if (exceptions.isDefined) exceptions.get.hashCode() else 0)
    result = 31 * result + (if (tags.isDefined) tags.get.hashCode() else 0)
    result
  }

  override def equals(o: scala.Any): Boolean = o match {
    case other: EventVO =>
      if (id != other.id) return false
      if (timeCreated != other.timeCreated) return false
      if (level != other.level) return false
      if (flag != other.flag) return false
      if (!(threadName == other.threadName)) return false
      if (!(loggerName == other.loggerName)) return false
      if (!(message == other.message)) return false
      if (!(callerStackTrace == other.callerStackTrace)) return false

      if (reserved.isEmpty) {
        if (other.reserved.isDefined)
          return false
      } else {
        val op = other.reserved
        if (op.isEmpty ||
          !(reserved.get == op.get))
          return false
      }
      val ots = other.tags
      if (tags.isEmpty) {
        if (ots.isDefined) return false
      } else {
        if (ots.isEmpty ||
          !(tags.get == ots.get))
          return false
      }
      val op = other.properties
      if (properties.isEmpty) {
        if (op.isDefined) return false
      } else {
        if (op.isEmpty ||
              !(properties.get == op.get))
          return false
      }
      val oes = other.exceptions
      if (exceptions.isEmpty) {
        if (oes.isDefined) return false
      } else {
        if (oes.isEmpty ||
          !exceptions.get.equals(oes.get))
          return false
      }
      true
    case _ => false
  }

  def appendJson(implicit json: JStrBuilder, prettyPrint: Boolean): JStrBuilder = {
    if (prettyPrint)
      return prettyJson(json)

    json.append("{\"id\":").append(id)
      .append(",\"timeCreated\":").append(timeCreated)
      .append(",\"level\":\"").append(level.toString)
      .append("\",\"loggerName\":\"")
    Helper.appendQuote(loggerName)
      .append("\",\"threadName\":\"")
    Helper.appendQuote(threadName)
      .append("\",\"flag\":").append(flag)
      .append(",\"message\":\"")
    Helper.appendQuote(message)

    json.append("\",\"callerStackTrace\":")
    Helper.stackTraceJson(callerStackTrace)

    if (reserved.isDefined)
      json.append(",\"reserved\":").append(reserved.get)

    if (exceptions.isDefined && exceptions.get.size > 0) {
      json.append(",\"exceptions\":[")
      exceptions.get.foreach(_.appendJson.append(','))
      json.setCharAt(json.length - 1, ']')
    }
    if (properties.isDefined && properties.get.size > 0) {
      json.append(",\"properties\":{")
      properties.get.foreach{t => json.append('\"')
        Helper.appendQuote(t._1)
          .append("\":\"")
        Helper.appendQuote(t._2.toString)
          .append("\",")}
      json.setCharAt(json.length - 1, '}')
    }
    if (tags.isDefined && tags.get.size > 0) {
      json.append(",\"tags\":[")
      tags.get.foreach{t=>json.append('\"')
        Helper.appendQuote(t)
          .append("\",")}
      json.setCharAt(json.length - 1, ']')
    }
    json.append('}')
  }

  def prettyJson(implicit json: JStrBuilder): JStrBuilder = {

    import Helper.appendQuote

    json.append("{\r\n  \"id\":").append(id)
    .append(",\r\n  \"timeCreated\":").append(timeCreated)
    .append(",\r\n  \"level\":\"").append(level.toString)
    .append("\",\r\n  \"loggerName\":\"")
    appendQuote(loggerName)
    .append("\",\r\n  \"threadName\":\"")
    appendQuote(threadName)
    .append("\",\r\n  \"flag\":").append(flag)
    .append(",\r\n  \"message\":\"")
    appendQuote(message)

    json.append("\",\r\n  \"callerStackTrace\":")
    Helper.prettyStackTraceJson(callerStackTrace)
    json.append(",\r\n")
    if (reserved.isDefined)
      json.append( """  "reserved":""")
        .append(reserved.get)
        .append(",\r\n")

    if (exceptions.isDefined && exceptions.get.size > 0) {
      json.append( """  "exceptions":[""")
      exceptions.get.foreach(_.prettyJson.append(",\r\n"))
      json.deleteCharAt(json.length - 3)
        .append("],\r\n")
    }
    if (properties.isDefined && properties.get.size > 0) {
      json.append("  \"properties\":{\r\n")
      properties.get.foreach{t => json.append("\t\"")
                        appendQuote(t._1)
                        .append("\":\"")
                        appendQuote(t._2.toString)
                          .append("\",\r\n")}

      json.deleteCharAt(json.length - 3)
      json.append("\t},\r\n")
    }
    if (tags.isDefined && tags.get.size > 0) {
      json.append( """  "tags":[""")
      tags.get.foreach{t=>json.append('\"')
          appendQuote(t)
          .append("\",")}
      json.deleteCharAt(json.length - 1)
      json.append("],\r\n")
    }
    json.deleteCharAt(json.length - 3)
    json.append('}')
  }

  override def toString: String = prettyJson(new JStrBuilder(512)).toString
}
object EventVO {

  val NA_ST = new StackTraceElement("?", "?", "?", -1)

  import Helper._

  //   |<-----  32  ----->|<-- 16 -->|<-- 16 -->|
  //          exception       tags        props
  //
  def buildFlag(prop: Map[_, _], tags: Set[String], exceptions: Vector[ExceptionVO]): Long = {
    val sz1: Int = if (exceptions != null) exceptions.size else 0

    val sz11: Short = if (tags != null) tags.size.toShort else 0
    val sz12: Short = if (prop != null) prop.size.toShort else 0

    val sz2: Int = add(sz11, sz12)

    add(sz1, sz2)
  }
  
  @inline def exceptionCount(flag: Long) = part1(flag)

  @inline def tagCount(flag: Long) = part1(part2(flag))

  @inline def propertyCount(flag: Long) = part2(part2(flag))

  @inline def hasExceptions(flag: Long) = flag > 4294967295L

  @inline def hasTags(flag: Long) = tagCount(flag) > 0

  @inline def hasProperties(flag: Long): Boolean = propertyCount(flag) > 0

}