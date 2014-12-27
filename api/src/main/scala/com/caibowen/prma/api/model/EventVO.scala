package com.caibowen.prma.api.model

import com.caibowen.prma.api.LogLevel.LogLevel

/**
* @author BowenCai
* @since  02/12/2014.
*/
@SerialVersionUID(-8179577194579626226L)
class EventVO(val id: Long,
              val timeCreated: Long,
              val level: LogLevel,
              val loggerName: String,
              val threadName: String,
              val callerStackTrace: StackTraceElement,
              val flag: Long,
              val message: String,
              val reserved: Option[Long],
              val properties: Option[Map[String, AnyRef]],
              val exceptions: Option[List[ExceptionVO]],
              val tags: Option[Set[String]]) extends Serializable {

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
           properties: Map[String, AnyRef],
           exceptions: List[ExceptionVO],
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
    result = 31 * result + (if (properties.isDefined) properties.get.hashCode else 0)
    result = 31 * result + (if (exceptions.isDefined) exceptions.get.hashCode else 0)
    result = 31 * result + (if (tags.isDefined) tags.get.hashCode else 0)
    return result
  }

  override def equals(o: scala.Any): Boolean = o match {
    case other: EventVO => {

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
          !(exceptions.get == oes.get))
          return false
      }

      true
    }
    case _ => false
  }


  def appendJson(implicit json: StringBuilder): StringBuilder = {
    json.append(
s"""{
  "id":$id,
  "timeCreated":$timeCreated,
  "level":"${level.toString}",
  "loggerName":"$loggerName",
  "threadName":"$threadName",
  "flag":$flag,
  "message":"$message",
""")

    json.append("  \"callerStackTrace\":")
    ExceptionVO.stackTraceJson(callerStackTrace)
    json.append(",\r\n")
    if (reserved.isDefined)
      json.append( """  "reserved":""")
        .append(reserved.get)
        .append(",\r\n")

    if (exceptions.isDefined && exceptions.get.size > 0) {
      json.append( """  "exceptions":[""")
      exceptions.get.foreach(_.appendJson.append(",\r\n"))
      json.deleteCharAt(json.length - 3)
        .append("],\r\n")
    }
    if (properties.isDefined && properties.get.size > 0) {
      json.append("  \"properties\":{\r\n")
      properties.get.foreach((t: (Any, Any))
                    => json.append("\t\"").append(t._1)
                        .append("\":\"").append(t._2).append("\",\r\n"))
      json.deleteCharAt(json.length - 3)
      json.append("\t},\r\n"
      )
    }
    if (tags.isDefined && tags.get.size > 0) {
      json.append( """  "tags":[""")
      tags.get.foreach(json.append('\"').append(_).append("\","))
      json.deleteCharAt(json.length - 1)
      json.append("],\r\n")
    }
    json.deleteCharAt(json.length - 3)
    json.append('}')
  }

  override def toString: String = appendJson(new StringBuilder(512)).toString

  def exceptionCount: Int = {
    return EventVO.part1(flag)
  }

  def markerCount: Int = {
    return EventVO.part1(EventVO.part2(flag))
  }

  def propertyCount: Int = {
    return EventVO.part2(EventVO.part2(flag))
  }

}
object EventVO {

  def buildFlag(prop: Map[_, _], markers: Set[String], exceptions: List[ExceptionVO]): Long = {
    val sz1: Int = if (exceptions != null) exceptions.size else 0

    val sz11: Short = if (markers != null) markers.size.toShort else 0
    val sz12: Short = if (prop != null) prop.size.toShort else 0

    val sz2: Int = add(sz11, sz12)

    add(sz1, sz2)
    // except mk prop
  }

  @inline
  def hasException(flag: Long): Boolean = {
    return flag > 4294967296L
  }

  @inline
  def hasMarkers(flag: Long): Boolean = {
    return flag > 65536L
  }

  @inline
  def hasProperty(flag: Long): Boolean = {
    throw new UnsupportedOperationException
  }

  private def add(a: Short, b: Short): Int = {
    return (a.toInt << 16) | (b.toInt & 0xFFFF)
  }

  private def add(a: Int, b: Int): Long = {
    return (a.toLong << 32) | (b.toLong & 0xFFFFFFFFL)
  }

  private def part1(c: Long): Int = {
    return (c >> 32).toInt
  }

  private def part2(c: Long): Int = {
    return c.toInt
  }

  private def part1(c: Int): Short = {
    return (c >> 16).toShort
  }

  private def part2(c: Int): Short = {
    return c.toShort
  }
}