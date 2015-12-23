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
import com.changhongit.loving.entity.Warning;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.repository.WarningRepository;
import com.changhongit.loving.response.CommonResponse;

public class WarningMessageHandler extends UntypedActor implements
		MessageHandler {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ActorRef mainHandler;
	
	final ApplicationContext ctx;
	
	private WarningRepository warningRepository;
	
	private KafkaProducerConfiguration producerConfiguration;
	
	private static final String WARNING_TOPIC = "warning.kafka.topic";
	
	private MessagePack messagePack;
	
	public WarningMessageHandler(ActorRef mainHandler, ApplicationContext ctx) {
		this.mainHandler = mainHandler;
		this.ctx = ctx;
		warningRepository = ctx.getBean(WarningRepository.class);
		producerConfiguration = ctx.getBean(KafkaProducerConfiguration.class);
		messagePack = ctx.getBean(MessagePack.class);
	}
	
	@Override
	public boolean matchType(String message) {
		return message.startsWith("C P") && message.contains("|0x0004|");
	}
	
	@Override
	public String getResponse(String message) {
		String[] params = message.split(" ");
		CommonResponse response = new CommonResponse(params[2], params[3], "0");
		return response.formatToString();
	}
	
	@Override
	public void processMessage(String message) {
		Warning warning = saveMessage(message);
		pushToRabbitMQ(warning);
	}
	
	private void pushToRabbitMQ(Warning warninng) {
		try {
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBytes = messagePack.write(warninng);
			KeyedMessage<String, byte[]> keyedMessage = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(WARNING_TOPIC), messageBytes);
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
				log.debug("message matched warning.");
				processMessage(message);
				String response = getResponse(message);
				if (!StringUtils.isEmpty(response)) {
					mainHandler.tell(new SendMessage(response), getSelf());
				}
			}
		}
	}
	
	private Warning saveMessage(String message) {
		String[] params = message.split(" ");
		String imei = params[3];
		String[] messageData = params[4].split(",");
		Warning warning = new Warning();
		warning.setImei(imei);
		warning.setDate(new Date());
		warning.setGpsStatus(Integer.parseInt(messageData[1]));
		if (warning.getGpsStatus() == 1) {
			warning.setLongitude(formateGps(messageData[2]));
			warning.setLatitude(formateGps(messageData[4]));
			if (messageData[3].equals("W")) {
				warning.setLongitude(0 - warning.getLongitude());
			}
			if (messageData[5].equals("S")) {
				warning.setLatitude(0 - warning.getLatitude());
			}
		}
		warning.setMcc(messageData[6]);
		warning.setMnc(messageData[7]);
		warning.setLac(messageData[8]);
		warning.setCell(messageData[9]);
		warning.setBatteralLevel(Integer.parseInt(messageData[10]));
		warning.setPedometer(messageData[11]);
		warning.setWarningFlag(messageData[12]);
		if (messageData.length == 14) {
			warning.setSpeed(Float.parseFloat(messageData[13]));
		}
		return warningRepository.save(warning);
	}
	
	private float formateGps(String part) {
		String intDegree = "0";
		String intMinute = "0";
		String floatMinute = "0";
		if (part.contains(".")) {
			String[] parts = part.split("\\.");
			int parts0Length = parts[0].length();
			if (parts0Length > 2) {
				intDegree = parts[0].substring(0, parts0Length - 2);
				intMinute = parts[0].substring(parts0Length - 2, parts0Length);
				floatMinute = parts[1];
			} else {
				intMinute = parts[0];
			}
		} else {
			int partLength = part.length();
			if (partLength > 2) {
				
				intDegree = part.substring(0, partLength - 2);
				intMinute = part.substring(partLength - 2, partLength);
			} else {
				intMinute = part;
			}
		}
		float floatDegree = Float.parseFloat(String.format("%s.%s", intMinute,
				floatMinute)) / 60;
		float result = Integer.parseInt(intDegree) + floatDegree;
		return result;
	}
}
