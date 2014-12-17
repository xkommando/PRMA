package com.caibowen.prma.logger.test

import java.util.concurrent.TimeUnit
import javax.sql.DataSource

import akka.actor.{ActorRef, ActorSystem}
import com.caibowen.gplume.scala.context.{AppContext, ContextBooter}
import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.core.ActorBuilder
import org.junit.After

import scala.concurrent.duration.Duration

/**
 * @author BowenCai
 * @since  08/12/2014.
 */
class DBContext {

  val manifestPath: String = "classpath:store_assemble.xml"

  def _prepare() : Unit = {

    val _bootstrap = new ContextBooter
    _bootstrap.setClassLoader(this.getClass.getClassLoader)
    _bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(this.getClass.getClassLoader))
    _bootstrap.setManifestPath(manifestPath)

    val actorSystem = ActorSystem("test-prma-actor")
    AppContext.beanAssembler.addBean("prma::internal::store::" + ActorBuilder.RootActorSystemBeanID, actorSystem)

    _bootstrap.boot

    println(AppContext.beanAssembler.getBean("eventStore"))
  }

  _prepare()

  val dataSource = AppContext.beanAssembler.getBean("dataSource").asInstanceOf[DataSource]
  val jdbcSupport = new JdbcSupport(dataSource)
  jdbcSupport setTraceSQL false

  val eventStore = AppContext.beanAssembler.getBean("eventStore").asInstanceOf[ActorRef]
  val actSys = AppContext.beanAssembler.getBean(ActorBuilder.RootActorSystemBeanID).asInstanceOf[ActorSystem]


  @After
  def finish(): Unit = {

    actSys.shutdown()
    actSys.awaitTermination(Duration.apply(3000, TimeUnit.SECONDS))
    println("\r\n--------------------------------\r\ndone")
  }
}
