package com.changhongit.loving;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.util.ByteString;

import com.changhongit.loving.downactor.Cp150DownMessageActor;
import com.changhongit.loving.message.AuthFailed;
import com.changhongit.loving.message.AuthSucceed;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.message.UnRegisterConnection;
import com.changhongit.loving.upactor.AuthenticationHandler;
import com.changhongit.loving.upactor.HeartbeatMsgHandler;
import com.changhongit.loving.upactor.ResponseMessageHandler;
import com.changhongit.loving.upactor.ShortMessageUploadMsgHandler;
import com.changhongit.loving.upactor.ShutDownMessageHandler;
import com.changhongit.loving.upactor.WarningMessageHandler;

public class Handler extends UntypedActor {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ApplicationContext ctx;
	
	private ActorRef connection;
	
	private ActorRef authHandler;
	
	private String imei;
	
	List<ActorRef> upHandlers = new ArrayList<ActorRef>();
	
	List<ActorRef> downHandlers = new ArrayList<ActorRef>();
	
	public Handler(ApplicationContext ctx) {
		this.ctx = ctx;
		log.debug("cp150 new connection accepted.");
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		
		authHandler = getContext().actorOf(
				Props.create(AuthenticationHandler.class, getSelf(), ctx));
		
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		
		if (msg instanceof Received) {
			
			connection = getSender();
			final ByteString data = ((Received) msg).data();
			String recievedData = data.utf8String();
			String[] recievedMessages = recievedData.split("\r\n");
			for (String recievedMessage : recievedMessages) {
				log.debug("Recieved data: {}", recievedMessage);
				if (StringUtils.isEmpty(imei)) {
					authHandler.tell(recievedMessage.trim(), getSelf());
				} else {
					for (ActorRef handler : upHandlers) {
						handler.tell(recievedMessage.trim(), getSelf());
					}
				}
			}
		} else if (msg instanceof SendMessage) {
			SendMessage message = (SendMessage) msg;
			connection.tell(TcpMessage.write(ByteString.fromString(message
					.getMessage())), ActorRef.noSender());
			log.debug("sent response/message --> {}", message.getMessage());
		} else if (msg instanceof Cp150DownMessage) {
			
			for (ActorRef downHandler : downHandlers) {
				downHandler.tell(msg, getSelf());
			}
			
		} else if (msg instanceof AuthSucceed) {
			
			AuthSucceed authSucceed = (AuthSucceed) msg;
			imei = authSucceed.getImei();
			createMessageHandlers();
			
		} else if (msg instanceof AuthFailed) {
			stopSelf();
		} else if (msg instanceof ConnectionClosed) {
			stopSelf();
		} else if (msg.equals("close")) {
			stopSelf();
		}
	}
	
	private void createMessageHandlers() {
		upHandlers.add(getContext().actorOf(
				Props.create(HeartbeatMsgHandler.class, getSelf(), ctx)));
		upHandlers.add(getContext()
				.actorOf(
						Props.create(ShortMessageUploadMsgHandler.class,
								getSelf(), ctx)));
		upHandlers.add(getContext().actorOf(
				Props.create(ShutDownMessageHandler.class, getSelf(), ctx)));
		upHandlers.add(getContext().actorOf(
				Props.create(WarningMessageHandler.class, getSelf(), ctx)));
		
		upHandlers.add(getContext().actorOf(
				Props.create(ResponseMessageHandler.class, getSelf(), ctx)));
		
		downHandlers.add(getContext().actorOf(
				Props.create(Cp150DownMessageActor.class, getSelf(), ctx)));
	}
	
	private void stopSelf() {
		authHandler.tell(new UnRegisterConnection(imei), getSelf());
		ActorSystem system = getContext().system();
		system.scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS),
				new Runnable() {
					
					@Override
					public void run() {
						getContext().stop(getSelf());
						log.debug("cp150 connection closed.");
						
					}
				}, system.dispatcher());
	}
}
