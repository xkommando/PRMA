package com.caibowen.prma.logger.test

import com.caibowen.prma.monitor.eval.Response
import com.caibowen.prma.monitor.notify.HttpNotify
import org.junit.Test

/**
 * @author BowenCai
 * @since  20/12/2014.
 */
class MonitorTest extends BuildContext {

  override def manifestPath = "classpath:monitor_assemble.xml"
  override def actorBeanPrefix = "prma::test::monitor::"

  @Test
  def t1(): Unit = {

//    val monitor = AppContext.beanAssembler.getBean("tMonitor1").asInstanceOf[ActorRef]
//    val vo = LogBackTest.gen
//    monitor ! vo

    val n = new HttpNotify("http://cbw-prma.appspot.com/notify", "cbw","psw");
    n.take(new Response(LogBackTest.gen))


  }

}
