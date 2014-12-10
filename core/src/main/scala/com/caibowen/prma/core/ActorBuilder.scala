package com.caibowen.prma.core

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem}
import com.caibowen.gplume.context.IBeanAssembler
import com.caibowen.gplume.context.bean.{AssemblerAwareBean, IDAwareBean, InitializingBean}

/**
 *
 * for Gplume injection
 *
 * @author BowenCai
 * @since  10/12/2014.
 */
object ActorBuilder {
  var actorSystemBeanID = "PRMA_Global_ActorSystem"
}
trait ActorBuilder extends AssemblerAwareBean with IDAwareBean with InitializingBean {

  /**
   * automatically build actor and put actor to the assembler with original ID
   * @param factory
   * @return
   */
  def buildWith(factory: ActorRefFactory): ActorRef

  private[this] var _beanID: String = _
  private[this] var _assembler: IBeanAssembler = _
  override def setBeanID(id: String): Unit = {
    _beanID = id
  }

  override def setAssembler(assembler: IBeanAssembler): Unit = {
    _assembler = assembler
  }

  override def afterPropertiesSet(): Unit = {
    val actorSys = _assembler.getBean(ActorBuilder.actorSystemBeanID).asInstanceOf[ActorSystem]
    require(actorSys != null)
    val ref = buildWith(actorSys)
    _assembler.removeBean(_beanID)
    _assembler.addBean(_beanID, ref)
    return
  }



}
