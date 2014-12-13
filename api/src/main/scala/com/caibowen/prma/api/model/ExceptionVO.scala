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

  override def toString: String = s"""com.caibowen.prma.api.model.ExceptionVO{" +
                    id=$id
                    , exceptionName=$name
                    , exceptionMessage=$message
                    , stackTraces=$stackTraces
                    }"""

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
}