package com.caibowen.prma.core

import akka.actor.{ActorRef, ActorRefFactory}
import com.caibowen.gplume.context.IBeanAssembler
import com.caibowen.gplume.context.bean.{AssemblerAwareBean, IDAwareBean, InitializingBean}
import com.caibowen.gplume.misc.Str.Utils._

import scala.beans.BeanProperty

/**
 *
 * for Gplume injection
 *
 * this bean will create actor and replace original ActorBuilder with ActorRef
 *
 * @author BowenCai
 * @since  10/12/2014.
 */
object ActorBuilder {
  var RootActorSystemBeanID = "PRMA_Root_ActorSystem"
}
trait ActorBuilder extends AssemblerAwareBean with IDAwareBean with InitializingBean {

  @BeanProperty var supervisorBean: String = _

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

  /**
   * replace builder bean with the actual actor
   * @return
   */
  override def afterPropertiesSet(): Unit = {
    val supervisorName = if (notBlank(supervisorBean)) supervisorBean
                          else ActorBuilder.RootActorSystemBeanID
    
    val actorSys = _assembler.getBean(supervisorName).asInstanceOf[ActorRefFactory]
    require(actorSys != null)
    val ref = buildWith(actorSys)
    _assembler.removeBean(_beanID)
    _assembler.addBean(_beanID, ref)
    return
  }



}
