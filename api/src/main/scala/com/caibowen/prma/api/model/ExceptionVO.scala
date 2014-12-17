package com.caibowen.prma.api.model

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
@SerialVersionUID(8087093751948611040L)
class ExceptionVO(@BeanProperty val id: Long,
                  @BeanProperty val name: String,
                  @BeanProperty val message: Option[String],
                  @BeanProperty val stackTraces: Option[List[StackTraceElement]]) extends Serializable {

  def this(eN: String, eMsg: String, sts: List[StackTraceElement]) {
    this(ExceptionVO.calculateID(eN, eMsg, sts), eN,
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

  def appendJson(implicit json: StringBuilder): StringBuilder = {
    val extra = 100 + (if (stackTraces.isDefined) stackTraces.get.size * 128 else 0)
    json.ensureCapacity(json.capacity + extra)

    json.append("{\r\n\"id\":").append(id)
      .append(",\r\n\"name\":\"").append(name).append("\"")
    if (message.isDefined)
      json.append(",\r\n\"message\":\"").append(message.get).append('\"')

    if (stackTraces.isDefined && stackTraces.get.size > 0) {
      json.append(",\r\n\"stackTraces\":[")
      stackTraces.get.foreach(ExceptionVO.stackTraceJson(_).append(",\r\n"))
      json.deleteCharAt(json.length() - 3)
      json.append("],\r\n")
    }
    json.append('}')
  }

  override def toString: String = appendJson(new StringBuilder(256)).toString

  override def equals(obj: scala.Any): Boolean = {
    if (this == obj)
      return true
    if (!obj.isInstanceOf[ExceptionVO])
      return false
    val that = obj.asInstanceOf[ExceptionVO]

    if (id != that.id) return false

    if (!name.equals(that.name)) return false

    if (message.isEmpty) {
      if (that.message.isDefined) return false
    } else {
      if (!message.get.equals(that.message.get)) return false
    }

    if (stackTraces.isEmpty) {
      if (that.stackTraces.isDefined) return false
    } else {
      if (!stackTraces.get.equals(that.stackTraces.get)) return false
    }
    true
  }
}
object ExceptionVO {
  @inline
  def calculateID(exceptionName: String,
                  exceptionMessage: String,
                  stackTraces: List[StackTraceElement]): Long = {

    //      MurmurHash
    var expID: Long = exceptionName.hashCode.toLong
    if (exceptionMessage != null) {
//    (v << n) | (v >>> (64 - n))
      val msgHash = exceptionMessage.hashCode
      expID = ((expID << msgHash) | (expID >>> (64 - msgHash)))
    }
    expID
  }

  @inline
  def stackTraceJson(st: StackTraceElement)(implicit json: StringBuilder):StringBuilder =
    json.append("{ \"file\":\"").append(st.getFileName)
      .append("\",\r\n\"class\":\"").append(st.getClassName)
      .append("\",\r\n\"function\":\"").append(st.getMethodName)
      .append("\",\r\n\"line\":").append(st.getLineNumber).append(" }")

//  @inline
//  def appendJson()
}