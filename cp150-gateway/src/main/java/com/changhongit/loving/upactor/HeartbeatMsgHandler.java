package com.changhongit.loving.upactor;

import java.text.SimpleDateFormat;
import java.util.Date;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.MessageHandler;
import com.changhongit.loving.entity.HeartBeat;
import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.repository.HeartbeatRepository;
import com.changhongit.loving.repository.MessagesToCp150Repository;

public class HeartbeatMsgHandler extends UntypedActor implements MessageHandler {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ActorRef mainHandler;
	
	final ApplicationContext ctx;
	
	private HeartbeatRepository heartbeatRepository;
	
	private MessagesToCp150Repository messagesToCp150Repository;
	
	private MessagePack messagePack;
	
	private KafkaProducerConfiguration producerConfiguration;
	
	private static final String HEATBEAT_TOPIC = "heartbeat.kafka.topic";
	
	public HeartbeatMsgHandler(ActorRef mainHandler, ApplicationContext ctx) {
		this.mainHandler = mainHandler;
		this.ctx = ctx;
		heartbeatRepository = ctx.getBean(HeartbeatRepository.class);
		messagesToCp150Repository = ctx
				.getBean(MessagesToCp150Repository.class);
		producerConfiguration = ctx.getBean(KafkaProducerConfiguration.class);
		messagePack = ctx.getBean(MessagePack.class);
	}
	
	@Override
	public boolean matchType(String message) {
		return message.startsWith("C P") && message.contains("|0x0003|");
	}
	
	@Override
	public String getResponse(String message) {
		return null;
	}
	
	@Override
	public void processMessage(String message) {
		
		HeartBeat heartBeat = saveMessage(message);
		
		pushToKafka(heartBeat);
		
		findAndSendUnSuccessMessage(message);
	}
	
	private void pushToKafka(HeartBeat heartBeat) {
		try {
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] heartBeatMessage = messagePack.write(heartBeat);
			KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(HEATBEAT_TOPIC),
					heartBeatMessage);
			producer.send(message);
			producer.close();
		} catch (Exception e) {
			log.error("Error: ", e);
		}
	}
	
	private void findAndSendUnSuccessMessage(String message) {
		String[] params = message.split(" ");
		String imei = params[3];
		Pageable pageable = new PageRequest(0, 50);
		Page<MessagesToCp150> messagesToCp150s = messagesToCp150Repository
				.findByImei(imei, pageable);
		log.debug("{} unsent message were found. ",
				messagesToCp150s.getNumber());
		for (MessagesToCp150 messagesToCp150 : messagesToCp150s.getContent()) {
			if (messagesToCp150.getRetryTimes() > 2) {
				log.error(
						"one of the messages sent to cp150 failed or the message act service is not working, imei: {}",
						messagesToCp150.getImei());
			} else if (new Date().getTime()
					- messagesToCp150.getSentDate().getTime() > 5 * 60 * 1000) {
				mainHandler.tell(new SendMessage(messagesToCp150.getMessage()),
						getSelf());
				log.debug("resend message to imei: {}, message: {}",
						messagesToCp150.getImei(), messagesToCp150.getMessage());
				messagesToCp150
						.setRetryTimes(messagesToCp150.getRetryTimes() + 1);
				messagesToCp150.setSentDate(new Date());
				messagesToCp150Repository.save(messagesToCp150);
			}
		}
	}
	
	private HeartBeat saveMessage(String message) {
		String[] params = message.split(" ");
		String imei = params[3];
		String[] messageData = params[4].split(",");
		HeartBeat heartBeat = new HeartBeat();
		heartBeat.setImei(imei);
		heartBeat.setDate(new Date());
		heartBeat.setGpsStatus(Integer.parseInt(messageData[1]));
		if (heartBeat.getGpsStatus() == 1) {
			
			heartBeat.setLongitude(formateGps(messageData[2]));
			heartBeat.setLatitude(formateGps(messageData[4]));
			if (messageData[3].equals("W")) {
				heartBeat.setLongitude(0 - heartBeat.getLongitude());
			}
			if (messageData[5].equals("S")) {
				heartBeat.setLatitude(0 - heartBeat.getLatitude());
			}
		}
		if (messageData[12].equals("0x0080")) {
			heartBeat.setChargeStatus("充电中");
		}
		
		heartBeat.setMcc(messageData[6]);
		if (messageData[7].equals("00")) {
			
			heartBeat.setMnc("0");
		} else {
			heartBeat.setMnc(messageData[7]);
			
		}
		heartBeat.setLac(messageData[8]);
		heartBeat.setCell(messageData[9]);
		heartBeat.setBatteralLevel(Integer.parseInt(messageData[10]));
		heartBeat.setPedometer(messageData[11]);
		if (messageData.length == 14) {
			heartBeat.setSpeed(Float.parseFloat(messageData[13]));
		}
		heartbeatRepository.save(heartBeat);
		return heartBeat;
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
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			String message = (String) msg;
			if (matchType(message)) {
				log.debug("message matched heartbeat.");
				processMessage(message);
				String response = getResponse(message);
				if (!StringUtils.isEmpty(response)) {
					mainHandler.tell(new SendMessage(response), getSelf());
				}
			}
		}
	}
}
