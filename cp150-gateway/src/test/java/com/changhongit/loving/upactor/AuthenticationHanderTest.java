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

public class AuthenticationHanderTest {

    TestActorRef<AuthenticationHandler> authenticationHandler = TestActorRef.create(ActorSystem.create("test", ConfigFactory.load("test")), Props.create(AuthenticationHandler.class, null, Mockito.mock(ApplicationContext.class)));

    AuthenticationHandler handler;

    @Before
    public void before(){
        handler = authenticationHandler.underlyingActor();
    }

    @Test
    public void matchType_rightMessage_returnTrue(){

        String authMessage = "C P 140224165507000 357718860201288 |0x00df|0x0005|0,1.04,00000000000000000000000000000000,5f2319b7989492c2b66b91624dddec8d,cfcd208495d565ef66e7dff9f98764da,cfcd208495d565ef66e7dff9f98764da,f1b708bba17f1ce948dc979f4d7092bc，cfcd208495d565ef66e 0x384f";
        Assert.assertTrue(handler.isValidAuthMessage(authMessage));
    }

    @Test
    public void getResponse_rightMessage_returnExpectedResponse(){

        String authMessage = "C P 140224165507000 357718860201288 |0x00df|0x0005|0,1.04,00000000000000000000000000000000,5f2319b7989492c2b66b91624dddec8d,cfcd208495d565ef66e7dff9f98764da,cfcd208495d565ef66e7dff9f98764da,f1b708bba17f1ce948dc979f4d7092bc，cfcd208495d565ef66e 0x384f";
        String expectedResponse = "A R 140224165507000 357718860201288 |0x002E|0 0x09E1\r\n";
        Assert.assertEquals(expectedResponse, handler.getResponse(authMessage));
    }


}
