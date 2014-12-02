package com.caibowen.prma.api.model

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  02/12/2014.
 */
@SerialVersionUID(8087093751948611040L)
class ExceptionVO(@BeanProperty val id: Long,
                  @BeanProperty val exceptionName: String,
                  @BeanProperty val exceptionMessage: Option[String],
                  @BeanProperty val stackTraces: Option[List[StackTraceElement]]) extends Serializable {

  def this(eN: String, eMsg: String, sts: List[StackTraceElement]) {
    this(ExceptionVO.calculateID(eN, eMsg, sts), eN,
      if (eMsg == null) None else Some(eMsg),
      if(sts == null) None else Some(sts)
    )
  }


  override def hashCode(): Int = {
    var result =  (id ^ (id >>> 32)).toInt
    result = 31 * result + exceptionName.hashCode
    result = if (exceptionMessage.isEmpty)
              31 * result
            else 31 * result + exceptionMessage.hashCode
    result = if (stackTraces.isEmpty)
              31 * result
            else 31 * result + stackTraces.hashCode
    result;
  }

  override def toString: String = s"""com.caibowen.prma.api.model.ExceptionVO{" +
                    id=$id
                    , exceptionName=$exceptionName
                    , exceptionMessage=$exceptionMessage
                    , stackTraces=$stackTraces
                    }"""

  override def equals(obj: scala.Any): Boolean = {
    if (this == obj)
      return true
    if (!obj.isInstanceOf[ExceptionVO])
      return false
    val that = obj.asInstanceOf[ExceptionVO]

    if (id != that.id) return false

    if (!exceptionName.equals(that.exceptionName)) return false

    if (exceptionMessage.isEmpty) {
      if (that.exceptionMessage.isDefined) return false
    } else {
      if (!exceptionMessage.get.equals(that.exceptionMessage.get)) return false
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
  def calculateID(exceptionName: String,
                  exceptionMessage: String,
                  stackTraces: List[StackTraceElement]): Long = {

    var expID: Long = exceptionName.hashCode.toLong
    if (exceptionMessage != null) expID |= exceptionMessage.hashCode & 0xFFFFFFFFL
    if (stackTraces == null || stackTraces.length == 0) return expID
    val kMul: Long = 0x9ddfea08eb382d69L
    for (st <- stackTraces) {
      val _s: Long = st.hashCode
      var _a: Long = ((expID) ^ _s) * kMul
      _a ^= _a >> 47
      expID = (_s ^ _a) * kMul
      expID ^= expID >> 47
      expID *= kMul
    }
    return expID
  }
}