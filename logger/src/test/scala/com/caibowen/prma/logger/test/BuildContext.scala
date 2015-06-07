package com.caibowen.prma.logger.test

import java.util.concurrent.TimeUnit
import javax.sql.DataSource

import akka.actor.ActorSystem
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import gplume.scala.context.{AppContext, ContextBooter}
import com.caibowen.prma.core.ActorBuilder
import org.junit.After

import scala.concurrent.duration.Duration

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
class BuildContext {

  def manifestPath  = "classpath:store_assemble.xml"
  def actorBeanPrefix = "prma::internal::actor::"

  def _prepare() : Unit = {

    val _bootstrap = new ContextBooter
    _bootstrap.setClassLoader(this.getClass.getClassLoader)
    _bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass.getClassLoader))
    _bootstrap.setManifestPath(manifestPath)
    _bootstrap.boot

    val actorSystem = ActorSystem("test-prma-actor")
    AppContext.beanAssembler.addBean(actorBeanPrefix + ActorBuilder.RootActorSystemBeanID, actorSystem)
  }

  _prepare()

  val actSys = AppContext.beanAssembler.getBean(ActorBuilder.RootActorSystemBeanID).asInstanceOf[ActorSystem]
  val dataSource = AppContext.beanAssembler.getBean("dataSource").asInstanceOf[DataSource]

  @After
  def finish(): Unit = {

    actSys.shutdown()
    actSys.awaitTermination(Duration.apply(3000, TimeUnit.SECONDS))
    println("\r\n--------------------------------\r\ndone")
  }
}
