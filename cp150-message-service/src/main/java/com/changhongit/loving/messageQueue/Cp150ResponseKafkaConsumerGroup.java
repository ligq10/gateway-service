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
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;

@Component
public class Cp150ResponseKafkaConsumerGroup implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String TOPIC = "response.kafka.topic";
	
	@Autowired
	private MessagePack messagePack;
	
	@Autowired
	private MessagesToCp150Repository messagesToCp150Repository;
	
	@Autowired
	private KafkaConsumerConfiguration kafkaConsumerConfiguration;
	
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(kafkaConsumerConfiguration.getTopic(TOPIC),
				new Integer(kafkaConsumerConfiguration.getNumThreads()));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = kafkaConsumerConfiguration
				.getResponseConsumerConnector().createMessageStreams(
						topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap
				.get(kafkaConsumerConfiguration.getTopic(TOPIC));
		ExecutorService executor = Executors
				.newFixedThreadPool(kafkaConsumerConfiguration.getNumThreads());
		int threadNumber = 0;
		for (KafkaStream stream : streams) {
			
			Cp150ResponseKafkaConsumer consumer = new Cp150ResponseKafkaConsumer(
					stream, messagePack);
			
			executor.submit(setConsumer(consumer));
			threadNumber++;
		}
	}
	
	private Cp150ResponseKafkaConsumer setConsumer(
			Cp150ResponseKafkaConsumer consumer) {
		consumer.setMessagesToCp150Repository(messagesToCp150Repository);
		return consumer;
	}
}