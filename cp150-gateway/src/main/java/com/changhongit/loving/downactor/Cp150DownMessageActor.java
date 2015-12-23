package com.changhongit.loving.downactor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class Cp150DownMessageActor extends UntypedActor {

    Logger log = LoggerFactory.getLogger(getClass());

    final ActorRef mainHandler;

    final ApplicationContext ctx;

    public Cp150DownMessageActor(ActorRef mainHandler, ApplicationContext ctx) {
        this.mainHandler = mainHandler;
        this.ctx = ctx;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Cp150DownMessage) {
            mainHandler.tell(new SendMessage(((Cp150DownMessage) msg).getMessage()), getSelf());
        }
    }


}
