package com.changhongit.loving.messageQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.KafkaConsumerConfiguration;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalStatusRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;

@Component
public class HeartbeatKafkaConsumerGroup implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessagePack messagePack;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private KafkaConsumerConfiguration kafkaConsumerConfiguration;
	
	@Autowired
	private TerminalStatusRepository terminalStatusRepository;
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		String topic = kafkaConsumerConfiguration
				.getTopic("heartbeat.kafka.topic");
		topicCountMap.put(topic,
				new Integer(kafkaConsumerConfiguration.getNumThreads()));
		ConsumerConnector consumerConnector = kafkaConsumerConfiguration
				.getHeartBeatConsumerConnector();
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
				.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		ExecutorService executor = Executors
				.newFixedThreadPool(kafkaConsumerConfiguration.getNumThreads());
		int threadNumber = 0;
		for (KafkaStream stream : streams) {
			HeartBeatKafkaConsumer heartBeatKafkaConsumer = new HeartBeatKafkaConsumer(
					stream, messagePack);
			executor.submit(setWarningKafkaConsumer(heartBeatKafkaConsumer));
			threadNumber++;
		}
	}
	
	private HeartBeatKafkaConsumer setWarningKafkaConsumer(
			HeartBeatKafkaConsumer consumer) {
		consumer.setEnv(env);
		consumer.setRestTemplate(restTemplate);
		consumer.setTerminalStatusRepository(terminalStatusRepository);
		consumer.setTerminalRepository(terminalRepository);
		return consumer;
	}
}