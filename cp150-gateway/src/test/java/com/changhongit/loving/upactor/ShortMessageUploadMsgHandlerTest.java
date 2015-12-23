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

public class ShortMessageUploadMsgHandlerTest {
    TestActorRef<ShortMessageUploadMsgHandler> actorRef = TestActorRef.create(ActorSystem.create("test", ConfigFactory.load("test")), Props.create(ShortMessageUploadMsgHandler.class, null, Mockito.mock(ApplicationContext.class)));

    ShortMessageUploadMsgHandler handler;

    @Before
    public void before(){
        handler = actorRef.underlyingActor();
    }

    @Test
    public void matchType_rightMessage_returnTrue(){

        String message = "C P 120209140502022 356584020999997 |??|0x0001|1,13522345431,大江东去 0x108A\r\n";
        Assert.assertTrue(handler.matchType(message));
    }

    @Test
    public void getResponse_rightMessage_returnExpectedResponse(){

        String message = "C P 120209140502022 356584020999997 |??|0x0001|1,13522345431,大江东去 0x108A\r\n";
        String expectedResponse = "A R 120209140502022 356584020999997 |0x002E|0 0x09ED\r\n";
        Assert.assertEquals(expectedResponse, handler.getResponse(message));
    }
}
