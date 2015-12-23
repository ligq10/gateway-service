package com.changhongit.loving.upactor;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.MessageHandler;
import com.changhongit.loving.msgQueue.ResponseMsgPack;

public class ResponseMessageHandler extends UntypedActor implements
		MessageHandler {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ActorRef mainHandler;
	
	final ApplicationContext ctx;
	
	private KafkaProducerConfiguration producerConfiguration;
	
	private static final String RESPONSE_TOPIC = "response.kafka.topic";
	
	private MessagePack messagePack;
	
	public ResponseMessageHandler(ActorRef mainHandler, ApplicationContext ctx) {
		this.mainHandler = mainHandler;
		this.ctx = ctx;
		producerConfiguration = ctx.getBean(KafkaProducerConfiguration.class);
		messagePack = ctx.getBean(MessagePack.class);
	}
	
	@Override
	public boolean matchType(String message) {
		return message.startsWith("A R");
	}
	
	@Override
	public String getResponse(String message) {
		return null;
	}
	
	@Override
	public void processMessage(String message) {
		
		String[] params = message.split(" ");
		String imei = params[3];
		String seq = params[2];
		ResponseMsgPack responseMsgPack = new ResponseMsgPack();
		responseMsgPack.setImei(imei);
		responseMsgPack.setSeq(seq);
		try {
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBeyts = messagePack.write(responseMsgPack);
			KeyedMessage<String, byte[]> keyedMessage = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(RESPONSE_TOPIC),
					messageBeyts);
			producer.send(keyedMessage);
			producer.close();
		} catch (Exception e) {
			log.error("Error: ", e);
		}
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			String message = (String) msg;
			if (matchType(message)) {
				log.debug("message matched response.");
				processMessage(message);
			}
		}
	}
	
}
