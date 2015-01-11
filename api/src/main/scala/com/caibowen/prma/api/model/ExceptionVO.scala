package com.caibowen.prma.api.model
import java.lang.{StringBuilder => JStrBuilder}

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
@SerialVersionUID(8087093751948611040L)
case class ExceptionVO(id: Long,
                       name: String,
                       message: Option[String],
                       stackTraces: Option[Vector[StackTraceElement]]) extends Serializable {

  require(name != null, "Exception name cannot be null")

  def this(eN: String, eMsg: String, sts: Vector[StackTraceElement]) {
    this(Helper.hashCombine(eN, eMsg), eN,
      if (eMsg == null) None else Some(eMsg),
      if(sts == null) None else Some(sts)
    )
  }

  override def hashCode(): Int = {
    var result =  (id ^ (id >>> 32)).toInt
    result = 31 * result + name.hashCode
    result = if (message.isEmpty)
              31 * result
            else 31 * result + message.hashCode
    result = if (stackTraces.isEmpty)
              31 * result
            else 31 * result + stackTraces.hashCode
    result
  }

  def prettyJson(implicit json: JStrBuilder): JStrBuilder = {
    val extra = 100 + (if (stackTraces.isDefined) stackTraces.get.size * 128 else 0)
    json.ensureCapacity(json.capacity + extra)

    json.append("{\r\n  \"id\":").append(id)
      .append(",\r\n  \"name\":\"").append(name).append("\"")
    if (message.isDefined)
      json.append(",\r\n  \"message\":\"")
      Helper.quote(message.get)
        .append('\"')

    if (stackTraces.isDefined && stackTraces.get.size > 0) {
      json.append(",\r\n  \"stackTraces\":[")
      stackTraces.get.foreach(Helper.prettyStackTraceJson(_).append(",\r\n"))
      json.deleteCharAt(json.length - 3)
      json.append("]\r\n")
    }
    json.append('}')
  }

  def appendJson(implicit json: JStrBuilder): JStrBuilder = {
    val extra = 100 + (if (stackTraces.isDefined) stackTraces.get.size * 128 else 0)
    json.ensureCapacity(json.capacity + extra)

    json.append("{\"id\":").append(id)
      .append(",\"name\":\"").append(name).append("\"")
    if (message.isDefined) {
      json.append(",\"message\":\"")
      Helper.quote(message.get)
        .append('\"')
    }
    if (stackTraces.isDefined && stackTraces.get.size > 0) {
      json.append(",\"stackTraces\":[")
      stackTraces.get.foreach(Helper.stackTraceJson(_).append(','))
      json.setCharAt(json.length - 1, ']')
    }
    json.append('}')
  }

  override def toString: String = prettyJson(new JStrBuilder(256)).toString

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: ExceptionVO => {
      if (this eq that)
        return true

      if (id != that.id) return false

      if (!(name == that.name)) return false

      val om = that.message
      if (message.isEmpty) {
        if (om.isDefined) return false
      } else {
        if (om.isEmpty || !(message.get equals  om.get)) return false
      }

      val oss = that.stackTraces
      if (stackTraces.isEmpty) {
        if (oss.isDefined) return false
      } else {
        if (oss.isEmpty || !(stackTraces.get == oss.get)) return false
      }

      true
  }
    case _ => false
  }
}