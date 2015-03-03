package com.caibowen.prma.logger.logback;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.caibowen.prma.api.EventAdaptor;
import com.caibowen.prma.core.ActorBuilder;
import com.caibowen.prma.core.ActorBuilder$;
import com.caibowen.prma.logger.Bootstrap;
import gplume.scala.context.AppContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bowen Cai on 2/23/2015.
 */
public class DBAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String configFile;

    ActorRef store;
    EventAdaptor<ILoggingEvent> adaptor;

    @Override
    public void start() {

        super.start();

//        System.out.println("preparing[" + configFile + "]");

        Bootstrap boot = new Bootstrap(configFile, this.getClass().getClassLoader());
        boot.boot();
        store =  AppContext.beanAssembler().getBean("eventStore");
        adaptor = AppContext.beanAssembler().getBean("logbackAdaptor");

//        System.out.println("started");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        store.tell(adaptor.from(eventObject), store);
    }

    @Override
    public void stop() {
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActorSystem actSys = AppContext.beanAssembler().getBean(ActorBuilder$.MODULE$.RootActorSystemBeanID());

        actSys.shutdown();
        actSys.awaitTermination(Duration.apply(3000, TimeUnit.SECONDS));
        System.out.println("stopped");

        super.stop();
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
