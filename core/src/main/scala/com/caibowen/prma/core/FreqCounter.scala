package com.caibowen.prma.core

import com.caibowen.gplume.common.collection.primitive.Int4CircularList

import scala.beans.BeanProperty

/**
 * @author BowenCai
 * @since  29/11/2014.
 */
@SerialVersionUID(-1151573363320687158L)
class FreqCounter extends Serializable {

  private [this] var OFFSET = System.currentTimeMillis()
  private [this] val record = new Int4CircularList(256)

  @BeanProperty var period : Double = 1.0

  def count(num : Int) : Unit = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this.synchronized {
      for (i <- 1 to num) record add now
      trimTo(now)
    }
  }

  def count() : Unit = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this synchronized {
      record add now
      trimTo(now)
    }
  }

  def reset(): Unit = {
    this synchronized {
      record.clear()
      OFFSET = System.currentTimeMillis
    }
  }

  def freq : Double = {
    var count = record.size.toDouble
    count = count / period
    count
  }

  def freqToNow : Double = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this.synchronized {
      trimTo(now)
    }
    freq
  }

  private def trimTo(now : Int) : Unit ={
    val limit = now - (1000 * this.period).toInt
    while (!record.isEmpty && record.front < limit)
      record.popFront()
  }

  def setBufferSize(bufferSize: Int): Unit = {
    record.ensureCapacity(bufferSize)
  }

}


