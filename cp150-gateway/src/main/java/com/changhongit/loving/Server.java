package com.changhongit.loving;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;

public class Server extends UntypedActor {

    Logger log = LoggerFactory.getLogger(getClass());

    final ActorRef manager;

    final ApplicationContext ctx;

    public Server(ActorRef manager, ApplicationContext ctx) {
        this.manager = manager;
        this.ctx = ctx;
    }

    @Override
    public void preStart() throws Exception {
        final ActorRef tcp = Tcp.get(getContext().system()).manager();
        Environment env = ctx.getBean(Environment.class);
        tcp.tell(
                TcpMessage.bind(
                        getSelf(),
                        new InetSocketAddress(
                                env.getRequiredProperty("cp150.gateway.host"),
                                Integer.parseInt(env
                                        .getRequiredProperty("cp150.gateway.port"))),
                        100), getSelf());
        log.info("cp150 gateway server preStart");
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Bound) {
            manager.tell(msg, getSelf());

        } else if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());
        } else if (msg instanceof Connected) {
            final Connected conn = (Connected) msg;
            manager.tell(conn, getSelf());
            final ActorRef handler = getContext().actorOf(
                    Props.create(Handler.class, ctx));

            getSender().tell(TcpMessage.register(handler), getSelf());
        }
    }
}
