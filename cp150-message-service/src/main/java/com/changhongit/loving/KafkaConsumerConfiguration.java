package com.changhongit.loving;

import java.util.Properties;

import javax.annotation.PostConstruct;

import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 73
 * 
 */
@Component
public class KafkaConsumerConfiguration {
	
	Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);
	
	@Autowired
	private Environment environment;
	
	ConsumerConnector heartBeatConsumerConnector;
	
	ConsumerConnector warningConsumerConnector;
	
	ConsumerConnector shortmessageConsumerConnector;
	
	ConsumerConnector settingConsumerConnector;
	
	ConsumerConnector responseConsumerConnector;
	
	@PostConstruct
	public void postConstruct() {
		String connect = environment.getProperty("kafka.zookeeper.connect");
		String timeout = environment
				.getProperty("kafka.zookeeper.session.timeout.ms");
		String time = environment.getProperty("kafka.zookeeper.sync.time.ms");
		String interval = environment
				.getProperty("kafka.auto.commit.interval.ms");
		String serializerClass = "kafka.serializer.DefaultEncoder";
		
		Properties heartBeatProps = new Properties();
		heartBeatProps.put("zookeeper.connect", connect);
		heartBeatProps.put("zookeeper.session.timeout.ms", timeout);
		heartBeatProps.put("zookeeper.sync.time.ms", time);
		heartBeatProps.put("auto.commit.interval.ms", interval);
		heartBeatProps.put("group.id",
				environment.getProperty("heartbeat.kafka.group.id"));
		heartBeatProps.put("serializer.class", serializerClass);
		heartBeatConsumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(heartBeatProps));
		
		Properties warningProps = new Properties();
		warningProps.put("zookeeper.connect", connect);
		warningProps.put("zookeeper.session.timeout.ms", timeout);
		warningProps.put("zookeeper.sync.time.ms", time);
		warningProps.put("auto.commit.interval.ms", interval);
		warningProps.put("group.id",
				environment.getProperty("warning.kafka.group.id"));
		warningProps.put("serializer.class", serializerClass);
		warningConsumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(warningProps));
		
		Properties shortmessageProps = new Properties();
		shortmessageProps.put("zookeeper.connect", connect);
		shortmessageProps.put("zookeeper.session.timeout.ms", timeout);
		shortmessageProps.put("zookeeper.sync.time.ms", time);
		shortmessageProps.put("auto.commit.interval.ms", interval);
		shortmessageProps.put("group.id",
				environment.getProperty("shortmessage.kafka.group.id"));
		shortmessageProps.put("serializer.class", serializerClass);
		shortmessageConsumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(
						shortmessageProps));
		
		Properties settingProps = new Properties();
		settingProps.put("zookeeper.connect", connect);
		settingProps.put("zookeeper.session.timeout.ms", timeout);
		settingProps.put("zookeeper.sync.time.ms", time);
		settingProps.put("auto.commit.interval.ms", interval);
		settingProps.put("group.id",
				environment.getProperty("setting.kafka.group.id"));
		settingProps.put("serializer.class", serializerClass);
		settingConsumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(settingProps));
		
		Properties responseProps = new Properties();
		responseProps.put("zookeeper.connect", connect);
		responseProps.put("zookeeper.session.timeout.ms", timeout);
		responseProps.put("zookeeper.sync.time.ms", time);
		responseProps.put("auto.commit.interval.ms", interval);
		responseProps.put("group.id",
				environment.getProperty("response.kafka.group.id"));
		responseProps.put("serializer.class", serializerClass);
		responseConsumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(responseProps));
	}
	
	public ConsumerConnector getHeartBeatConsumerConnector() {
		return heartBeatConsumerConnector;
	}
	
	public ConsumerConnector getWarningConsumerConnector() {
		return warningConsumerConnector;
	}
	
	public ConsumerConnector getShortmessageConsumerConnector() {
		return shortmessageConsumerConnector;
	}
	
	public ConsumerConnector getSettingConsumerConnector() {
		return settingConsumerConnector;
	}
	
	public ConsumerConnector getResponseConsumerConnector() {
		return responseConsumerConnector;
	}
	
	public String getTopic(String key) {
		return environment.getProperty(key);
	}
	
	public int getNumThreads() {
		String numThreads = environment
				.getProperty("kafka.consumer.thread.num");
		if (StringUtils.isEmpty(numThreads) == false) {
			return Integer.parseInt(numThreads);
		}
		return 1;
	}
}
