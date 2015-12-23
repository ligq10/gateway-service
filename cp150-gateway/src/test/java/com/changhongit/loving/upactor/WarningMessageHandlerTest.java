package com.changhongit.loving.upactor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

public class WarningMessageHandlerTest {

    TestActorRef<WarningMessageHandler> actorRef = TestActorRef.create(ActorSystem.create("test", ConfigFactory.load("test")), Props.create(WarningMessageHandler.class, null, Mockito.mock(ApplicationContext.class)));

    WarningMessageHandler handler;

    @Before
    public void before(){
        handler = actorRef.underlyingActor();
    }

    @Test
    public void matchType_rightMessage_returnTrue(){

        String message = "C P 120209140502022 356584020999997 |0x0064|0x0004|0,1,116.4664,E,39.08566,N,460,00,4461,26747,3,13e,0x0000, 0.052 0x108A\r\n";
        Assert.assertTrue(handler.matchType(message));
    }

    @Test
    public void getResponse_rightMessage_returnExpectedResponse(){

        String message = "C P 120209140502022 356584020999997 |0x0064|0x0004|0,1,116.4664,E,39.08566,N,460,00,4461,26747,3,13e,0x0000, 0.052 0x108A\r\n";
        String expectedResponse = "A R 120209140502022 356584020999997 |0x002E|0 0x09ED\r\n";
        Assert.assertEquals(expectedResponse, handler.getResponse(message));
    }
}
