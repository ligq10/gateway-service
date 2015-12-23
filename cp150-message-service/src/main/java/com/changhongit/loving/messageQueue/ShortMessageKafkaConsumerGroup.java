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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import akka.cluster.Cluster;

import com.changhongit.loving.KafkaConsumerConfiguration;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ShortMessageKafkaConsumerGroup implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String TOPIC = "shortmessage.kafka.topic";
	
	@Autowired
	private MessagePack messagePack;
	
	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private Cp150SettingRepository cp150SettingRepository;
	
	@Autowired
	private TerminalRepository terminalRestRepository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	@Autowired
	private MessagesToCp150Repository messagesToCp150Repository;
	
	@Autowired
	private SettingMessageConventer settingMessageConventer;
	
	@Autowired
	private Cluster cluster;
	
	@Autowired
	private KafkaConsumerConfiguration kafkaConsumerConfiguration;
	
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(kafkaConsumerConfiguration.getTopic(TOPIC),
				new Integer(kafkaConsumerConfiguration.getNumThreads()));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = kafkaConsumerConfiguration
				.getShortmessageConsumerConnector().createMessageStreams(
						topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap
				.get(kafkaConsumerConfiguration.getTopic(TOPIC));
		ExecutorService executor = Executors
				.newFixedThreadPool(kafkaConsumerConfiguration.getNumThreads());
		int threadNumber = 0;
		for (KafkaStream stream : streams) {
			
			ShortMessageKafkaConsumer consumer = new ShortMessageKafkaConsumer(
					stream, messagePack);
			
			executor.submit(setWarningKafkaConsumer(consumer));
			threadNumber++;
		}
	}
	
	private ShortMessageKafkaConsumer setWarningKafkaConsumer(
			ShortMessageKafkaConsumer consumer) {
		consumer.setCluster(cluster);
		consumer.setCp150SettingRepository(cp150SettingRepository);
		consumer.setEnv(env);
		consumer.setMessagesToCp150Repository(messagesToCp150Repository);
		consumer.setRestTemplate(restTemplate);
		consumer.setSettingMessageConventer(settingMessageConventer);
		consumer.setTerminalRestRepository(terminalRestRepository);
		consumer.setTerminalUserRepository(terminalUserRepository);
		consumer.setMessagesToCp150Repository(messagesToCp150Repository);
		return consumer;
	}
}