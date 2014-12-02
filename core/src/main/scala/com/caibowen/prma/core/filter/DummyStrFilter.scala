package com.caibowen.prma.core.filter

/**
 * @author BowenCai
 * @since  01/12/2014.
 */
class DummyStrFilter extends StrFilter(null) {

  override def parse(s: String): Unit ={

  }

  @inline
  override def doAccept(s: String): Int= {
    -1
  }

}
