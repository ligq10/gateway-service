package com.changhongit.loving;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Tcp;
import org.msgpack.MessagePack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.changhongit.loving.entity"})
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.changhongit.loving"})
public class GatewayLauncher {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(
                GatewayLauncher.class, args);

        ActorSystem system = ActorSystem.create("cp150");
        ActorRef manager = Tcp.get(system).manager();
        system.actorOf(Props.create(ConnectionManager.class), "cp150Connections");
        system.actorOf(Props.create(Server.class, manager, ctx), "server");

    }

    @Bean
    public MessagePack messagePack(){
        return new MessagePack();
    }

}
