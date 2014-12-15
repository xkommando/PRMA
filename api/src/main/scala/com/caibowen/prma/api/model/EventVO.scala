package com.caibowen.prma.api.model

import com.caibowen.prma.api.LogLevel.LogLevel

import scala.beans.BeanProperty

/**
* @author BowenCai
* @since  02/12/2014.
*/
@SerialVersionUID(-8179577194579626226L)
class EventVO(@BeanProperty val id: Long,
              @BeanProperty val timeCreated: Long,
              @BeanProperty val level: LogLevel,
              @BeanProperty val loggerName: String,
              @BeanProperty val threadName: String,
              @BeanProperty val callerStackTrace: StackTraceElement,
              @BeanProperty val flag: Long,
              @BeanProperty val message: String,
              @BeanProperty val reserved: Option[Long],
              @BeanProperty val properties: Option[Map[String, AnyRef]],
              @BeanProperty val exceptions: Option[List[ExceptionVO]],
              @BeanProperty val markers: Option[Set[String]]) extends Serializable {

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
    result = 31 * result + (if (markers.isDefined) markers.get.hashCode else 0)
    return result
  }

  override def equals(o: scala.Any): Boolean = o match {
    case eventVO: EventVO => {

      if (id != eventVO.id) return false
      if (timeCreated != eventVO.timeCreated) return false
      if (level ne eventVO.level) return false
      if (flag != eventVO.flag) return false
      if (!(threadName == eventVO.threadName)) return false
      if (!(loggerName == eventVO.loggerName)) return false
      if (!(message == eventVO.message)) return false

      if (if (reserved.isDefined) reserved.get == eventVO.reserved.get else eventVO.reserved.isDefined) return false
      if (!(callerStackTrace.equals(eventVO.callerStackTrace))) return false
      if (if (exceptions.isDefined) !(exceptions.get.equals(eventVO.exceptions)) else eventVO.exceptions.isDefined) return false
      if (if (markers.isDefined) !(markers.get.equals(eventVO.markers.get)) else eventVO.markers.isDefined) return false
      if (properties.isEmpty) {
        if (eventVO.properties.isDefined)
          return false
      } else {
        if (!properties.equals(eventVO.properties))
          return false
      }
      if (if (properties.isDefined)
            properties != eventVO.properties
          else eventVO.properties.isEmpty) {
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

    json.append("\"caller\":")
    ExceptionVO.appendJson(callerStackTrace)
    json.append(",\r\n")
    if (reserved.isDefined)
      json.append( """"reserved":""")
        .append(reserved.get)
        .append(",\r\n")

    if (exceptions.isDefined && exceptions.get.size > 0) {
      json.append( """"exceptions":[""")
      exceptions.get.foreach(_.appendJson.append(",\r\n"))
      json.deleteCharAt(json.length - 3)
        .append("],\r\n")
    }
    if (properties.isDefined && properties.get.size > 0) {
      json.append("\"properties\":{\r\n")
      properties.get.foreach((t: (Any, Any))
      => json.append("\t\"").append(t._1).append("\":\"").append(t._2).append("\",\r\n"))
      json.deleteCharAt(json.length - 3)
      json.append("\t},\r\n"
      )
    }
    if (markers.isDefined && markers.get.size > 0) {
      json.append( """"markers":[""")
      markers.get.foreach(json.append('\"').append(_).append("\","))
      json.deleteCharAt(json.length - 1)
      json.append("]\r\n")
    }
    json.append("}")
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