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
import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.SchemaConfiguration;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.jpaRepository.GroupRepository;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalStatusRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;
import com.changhongit.loving.jpaRepository.WarningDetailRepository;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WarningKafkaConsumerGroup implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String TOPIC = "warning.kafka.topic";
	
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
	private WarningDetailRepository warningDetailRepository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	@Autowired
	private TerminalStatusRepository terminalStatusRepository;
	
	@Autowired
	private MessagesToCp150Repository messagesToCp150Repository;
	
	@Autowired
	private KafkaProducerConfiguration kafkaProducerConfiguration;
	
	@Autowired
	private SchemaConfiguration schemaConfiguration;
	
	@Autowired
	private SettingMessageConventer settingMessageConventer;
	
	@Autowired
	private Cluster cluster;
	
	@Autowired
	private GroupRepository groupRestRepository;
	
	@Autowired
	private KafkaConsumerConfiguration kafkaConsumerConfiguration;
	
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(kafkaConsumerConfiguration.getTopic(TOPIC),
				new Integer(kafkaConsumerConfiguration.getNumThreads()));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = kafkaConsumerConfiguration
				.getWarningConsumerConnector().createMessageStreams(
						topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap
				.get(kafkaConsumerConfiguration.getTopic(TOPIC));
		ExecutorService executor = Executors
				.newFixedThreadPool(kafkaConsumerConfiguration.getNumThreads());
		int threadNumber = 0;
		for (KafkaStream stream : streams) {
			
			WarningKafkaConsumer warningKafkaConsumer = new WarningKafkaConsumer(
					stream, messagePack);
			
			executor.submit(setWarningKafkaConsumer(warningKafkaConsumer));
			threadNumber++;
		}
	}
	
	private WarningKafkaConsumer setWarningKafkaConsumer(
			WarningKafkaConsumer consumer) {
		consumer.setCluster(cluster);
		consumer.setCp150SettingRepository(cp150SettingRepository);
		consumer.setEnv(env);
		consumer.setGroupRestRepository(groupRestRepository);
		consumer.setKafkaProducerConfiguration(kafkaProducerConfiguration);
		consumer.setMessagesToCp150Repository(messagesToCp150Repository);
		consumer.setObjectMapper(objectMapper);
		consumer.setRestTemplate(restTemplate);
		consumer.setSchemaConfiguration(schemaConfiguration);
		consumer.setSettingMessageConventer(settingMessageConventer);
		consumer.setTerminalRestRepository(terminalRestRepository);
		consumer.setTerminalStatusRepository(terminalStatusRepository);
		consumer.setTerminalUserRepository(terminalUserRepository);
		consumer.setWarningDetailRepository(warningDetailRepository);
		return consumer;
	}
}