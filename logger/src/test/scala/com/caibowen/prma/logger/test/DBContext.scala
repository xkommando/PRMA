package com.caibowen.prma.logger.test

import javax.sql.DataSource

import akka.actor.{ActorRef, ActorSystem}
import com.caibowen.gplume.context.{AppContext, ContextBooter}
import com.caibowen.gplume.jdbc.JdbcSupport
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.core.ActorBuilder

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
    AppContext.beanAssembler.addBean("prma::internal::store::" + ActorBuilder.actorSystemBeanID, actorSystem)

    _bootstrap.boot

    println(AppContext.beanAssembler.getBean("eventStore"))
  }

  _prepare()

  val dataSource = AppContext.beanAssembler.getBean("dataSource").asInstanceOf[DataSource]
  val jdbcSupport = new JdbcSupport(dataSource)
  jdbcSupport setTraceSQL false
  val eventStore = AppContext.beanAssembler.getBean("eventStore").asInstanceOf[ActorRef]

}
