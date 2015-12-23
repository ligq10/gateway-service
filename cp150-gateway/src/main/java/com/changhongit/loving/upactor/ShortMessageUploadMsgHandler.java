package com.changhongit.loving.upactor;

import java.util.Date;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.MessageHandler;
import com.changhongit.loving.entity.ShortMessage;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.repository.ShortMessageRepository;
import com.changhongit.loving.response.CommonResponse;

public class ShortMessageUploadMsgHandler extends UntypedActor implements
		MessageHandler {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ActorRef mainHandler;
	
	final ApplicationContext ctx;
	
	private ShortMessageRepository shortMessageRepository;
	
	private KafkaProducerConfiguration producerConfiguration;
	
	private static final String SHORTMESSAGE_TOPIC = "shortmessage.kafka.topic";
	
	private MessagePack messagePack;
	
	public ShortMessageUploadMsgHandler(ActorRef mainHandler,
			ApplicationContext ctx) {
		this.mainHandler = mainHandler;
		this.ctx = ctx;
		shortMessageRepository = ctx.getBean(ShortMessageRepository.class);
		producerConfiguration = ctx.getBean(KafkaProducerConfiguration.class);
		messagePack = ctx.getBean(MessagePack.class);
	}
	
	@Override
	public boolean matchType(String message) {
		return message.startsWith("C P") && message.contains("|0x0001|");
	}
	
	@Override
	public String getResponse(String message) {
		String[] params = message.split(" ");
		CommonResponse response = new CommonResponse(params[2], params[3], "0");
		return response.formatToString();
	}
	
	@Override
	public void processMessage(String message) {
		ShortMessage shortMessage = saveMessage(message);
		try {
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBytes = messagePack.write(shortMessage);
			KeyedMessage<String, byte[]> keyedMessage = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(SHORTMESSAGE_TOPIC),
					messageBytes);
			producer.send(keyedMessage);
			producer.close();
		} catch (Exception e) {
			log.error("Error: ", e);
		}
	}
	
	private ShortMessage saveMessage(String message) {
		String[] params = message.split(" ");
		String imei = params[3];
		String[] messageData = params[4].split(",");
		ShortMessage shortMessage = new ShortMessage();
		shortMessage.setImei(imei);
		shortMessage.setDate(new Date());
		shortMessage.setSentFrom(messageData[1]);
		shortMessage.setMsgContent(params[4]);
		return shortMessageRepository.save(shortMessage);
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			String message = (String) msg;
			if (matchType(message)) {
				log.debug("message matched upload short message..");
				processMessage(message);
				String response = getResponse(message);
				if (!StringUtils.isEmpty(response)) {
					mainHandler.tell(new SendMessage(response), getSelf());
				}
			}
		}
	}
}
