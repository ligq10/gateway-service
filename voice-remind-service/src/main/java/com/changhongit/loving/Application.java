package com.changhongit.loving;

import java.text.SimpleDateFormat;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackages = { "com.changhongit.loving" })
@EnableAutoConfiguration()
public class Application {
	
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 1000;
	
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(
				Application.class, args);
		Environment env = ctx.getBean(Environment.class);
		ThreadPoolTaskScheduler threadPoolTaskScheduler = ctx
				.getBean(ThreadPoolTaskScheduler.class);
		RemindersIssue remindersIssue = ctx.getBean(RemindersIssue.class);
		RemindersExport remindersExport = ctx.getBean(RemindersExport.class);
		RemindersDeleteIssue remindersDeleteIssue = ctx
				.getBean(RemindersDeleteIssue.class);
		threadPoolTaskScheduler.schedule(remindersIssue,
				new CronTrigger(env.getProperty("remindersIssue.time")));
		threadPoolTaskScheduler.schedule(remindersExport,
				new CronTrigger(env.getProperty("remindersExport.time")));
		threadPoolTaskScheduler.schedule(remindersDeleteIssue, new CronTrigger(
				env.getProperty("remindersDeleteIssue.time")));
	}
	
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		return threadPoolTaskScheduler;
		
	}
	
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dateFormat);
		return objectMapper;
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
