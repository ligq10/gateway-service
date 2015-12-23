package com.changhongit.loving;

import org.msgpack.MessagePack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;

import com.typesafe.config.ConfigFactory;

@Configuration
@ComponentScan(basePackages = { "com.changhongit.loving" })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class })
@EnableJpaRepositories("com.changhongit.loving.jpaRepository")
@EnableMongoRepositories("com.changhongit.loving.repository")
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public Cluster cluster() {
		ActorSystem system = ActorSystem.create("cp150",
				ConfigFactory.load("remote.conf"));
		Cluster cluster = Cluster.get(system);
		return cluster;
	}
	
	@Bean
	public MessagePack messagePack() {
		return new MessagePack();
	}
}
