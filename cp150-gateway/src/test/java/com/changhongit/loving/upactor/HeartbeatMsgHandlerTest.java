package com.changhongit.loving.upactor;

import org.junit.Test;

public class HeartbeatMsgHandlerTest {
	
	// TestActorRef<HeartbeatMsgHandler> actorRef = TestActorRef.create(
	// ActorSystem.create("test", ConfigFactory.load("test")),
	// Props.create(HeartbeatMsgHandler.class, null,
	// Mockito.mock(ApplicationContext.class)));
	//
	// HeartbeatMsgHandler handler;
	//
	// @Before
	// public void before() {
	// handler = actorRef.underlyingActor();
	// }
	
	@Test
	public void matchType_rightMessage_returnTrue() {
		
		// String heartBeatMessage =
		// "C P 120209140502022 356584020999997 |0x0064|0x0003|0,1,10410.3,E,3041.97,N,460,00,4461,26747,3,13e,0.052 0x108A\r\n";
		// Assert.assertTrue(handler.matchType(heartBeatMessage));
		
	}
	
	@Test
	public void getResponse_rightMessage_returnExpectedResponse() {
		
		// String heartBeatMessage =
		// "C P 120209140502022 356584020999997 |0x0064|0x0003|0,1,10410.3,E,3041.97,N,460,00,4461,26747,3,13e,0.052 0x108A\r\n";
		// String expectedResponse = null;
		// Assert.assertEquals(expectedResponse,
		// handler.getResponse(heartBeatMessage));
		
	}
}
