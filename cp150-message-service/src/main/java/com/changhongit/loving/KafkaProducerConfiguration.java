package com.changhongit.loving;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KafkaProducerConfiguration {
	
	Logger logger = LoggerFactory.getLogger(KafkaProducerConfiguration.class);
	
	@Autowired
	private Environment environment;
	
	@Bean
	public Properties getProperties() {
		Properties props = new Properties();
		props.put("metadata.broker.list",
				environment.getProperty("metadata.broker.list"));
		props.put("serializer.class", "kafka.serializer.DefaultEncoder");
		props.put("request.required.acks",
				environment.getProperty("request.required.acks"));
		props.put("partitioner.class",
				"com.changhongit.loving.WarnningPartitioner");
		props.put("num.partitions", environment.getProperty("num.partitions"));
		props.put("log.retention.hours",
				environment.getProperty("log.retention.hours"));
		return props;
	}
	
	@Bean
	public ProducerConfig getProducerConfig() {
		return new ProducerConfig(getProperties());
	}
	
	@Bean
	public Producer<String, byte[]> getProducer() {
		return new Producer<String, byte[]>(this.getProducerConfig());
	}
	
}