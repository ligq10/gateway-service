package com.changhongit.loving.messageQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.KafkaStream;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.changhongit.loving.KafkaConsumerConfiguration;
import com.changhongit.loving.repository.Cp150ContactsRepository;

@Component
public class TerminalSettingKafkaConsumerGroup implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String TOPIC = "setting.kafka.topic";
	
	@Autowired
	private MessagePack messagePack;
	
	@Autowired
	private Cp150ContactsRepository contactsRepository;
	
	@Autowired
	private KafkaConsumerConfiguration kafkaConsumerConfiguration;
	
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(kafkaConsumerConfiguration.getTopic(TOPIC),
				new Integer(kafkaConsumerConfiguration.getNumThreads()));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = kafkaConsumerConfiguration
				.getSettingConsumerConnector().createMessageStreams(
						topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap
				.get(kafkaConsumerConfiguration.getTopic(TOPIC));
		ExecutorService executor = Executors
				.newFixedThreadPool(kafkaConsumerConfiguration.getNumThreads());
		int threadNumber = 0;
		for (KafkaStream stream : streams) {
			
			TerminalSettingKafkaConsumer consumer = new TerminalSettingKafkaConsumer(
					stream, messagePack);
			
			executor.submit(setConsumer(consumer));
			threadNumber++;
		}
	}
	
	private TerminalSettingKafkaConsumer setConsumer(
			TerminalSettingKafkaConsumer consumer) {
		consumer.setContactsRepository(contactsRepository);
		return consumer;
	}
}