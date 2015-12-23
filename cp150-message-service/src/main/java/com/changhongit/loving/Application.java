package com.changhongit.loving;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.msgpack.MessagePack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;

import com.changhongit.loving.messageQueue.Cp150ResponseKafkaConsumerGroup;
import com.changhongit.loving.messageQueue.HeartbeatKafkaConsumerGroup;
import com.changhongit.loving.messageQueue.ShortMessageKafkaConsumerGroup;
import com.changhongit.loving.messageQueue.TerminalSettingKafkaConsumerGroup;
import com.changhongit.loving.messageQueue.WarningKafkaConsumerGroup;
import com.typesafe.config.ConfigFactory;

@Configuration
@ComponentScan(basePackages = { "com.changhongit.loving" })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class })
@EnableJpaRepositories("com.changhongit.loving.jpaRepository")
@EnableMongoRepositories("com.changhongit.loving.repository")
public class Application extends RepositoryRestMvcConfiguration {
	
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 1000;
	
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(
				Application.class, args);
		Cp150ResponseKafkaConsumerGroup responseKafkaConsumerGroup = ctx
				.getBean(Cp150ResponseKafkaConsumerGroup.class);
		new Thread(responseKafkaConsumerGroup).start();
		ShortMessageKafkaConsumerGroup shortMessageKafkaConsumerGroup = ctx
				.getBean(ShortMessageKafkaConsumerGroup.class);
		new Thread(shortMessageKafkaConsumerGroup).start();
		
		WarningKafkaConsumerGroup warningKafkaConsumerGroup = ctx
				.getBean(WarningKafkaConsumerGroup.class);
		new Thread(warningKafkaConsumerGroup).start();
		
		TerminalSettingKafkaConsumerGroup settingKafkaConsumerGroup = ctx
				.getBean(TerminalSettingKafkaConsumerGroup.class);
		new Thread(settingKafkaConsumerGroup).start();
		
		HeartbeatKafkaConsumerGroup consumerGroup = ctx
				.getBean(HeartbeatKafkaConsumerGroup.class);
		new Thread(consumerGroup).start();
	}
	
	@Bean
	public MessagePack messagePack() {
		return new MessagePack();
	}
	
	@Bean
	public Cluster cluster() {
		ActorSystem system = ActorSystem.create("cp150",
				ConfigFactory.load("remote.conf"));
		Cluster cluster = Cluster.get(system);
		return cluster;
	}
	
	@Bean
	RestTemplate restTemplate() {
		
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager
				.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		HttpClientBuilder httpClient = HttpClientBuilder.create()
				.setConnectionManager(connectionManager);
		
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient.build());
		
		return new RestTemplate(httpRequestFactory);
	}
}
