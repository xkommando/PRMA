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
  private [this] val history = new Int4CircularList(256)

  @BeanProperty var period : Double = 1.0

  def count(num : Int) : Unit = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this.synchronized {
      for (i <- 1 to num) history.add(now)
      cut(now)
    }
  }

  def count() : Unit = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this synchronized {
      history.add(now)
      cut(now)
    }
  }

  def reset(): Unit = {
    this synchronized {
      history.clear()
      OFFSET = System.currentTimeMillis()
    }
  }

  def freq : Double = {
    var count : Double = history.size.toDouble
    count = count / period
    count
  }

  def freqToNow : Double = {
    val now : Int = (System.currentTimeMillis - OFFSET).toInt
    this.synchronized {
      cut(now)
    }
    freq
  }

  private def cut(now : Int) : Unit ={
    val limit = now - (1000 * this.period).toInt
    while (!history.isEmpty && history.front < limit)
      history.popFront()
  }

  def setBufferSize(bufferSize: Int): Unit = {
    history.ensureCapacity(bufferSize)
  }

}


