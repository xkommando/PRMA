package com.caibowen.prma.logger

import akka.actor.ActorSystem
import com.caibowen.gplume.resource.ClassLoaderInputStreamProvider
import com.caibowen.prma.core.ActorBuilder
import gplume.scala.context.{AppContext, ContextBooter}


/**
 * Created by Bowen Cai on 2/22/2015.
 */
class Bootstrap(manifestPath: String, classLoader: ClassLoader, actorBeanPrefix: String) {

  def this(manifestPath: String, classLoader: ClassLoader) {
    this(manifestPath, classLoader ,"prma::internal::store::")
  }

  def boot(): Unit = {
    val _bootstrap = new ContextBooter
    _bootstrap.setClassLoader(classLoader)
    _bootstrap.setStreamProvider(new ClassLoaderInputStreamProvider(classLoader))
    _bootstrap.setManifestPath(manifestPath)

    val actorSystem = ActorSystem(ActorBuilder.RootActorSystemBeanID)
    AppContext.beanAssembler.addBean(actorBeanPrefix + ActorBuilder.RootActorSystemBeanID, actorSystem)

    _bootstrap.boot
  }

}
