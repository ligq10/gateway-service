package com.changhongit.loving;

import java.text.SimpleDateFormat;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.validator.TerminalAddValidator;
import com.changhongit.loving.validator.TerminalDeleteValidator;
import com.changhongit.loving.validator.TerminalPatchValidator;
import com.changhongit.loving.validator.TerminalSaveValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableJpaRepositories
@ComponentScan(basePackages = { "com.changhongit.loving" })
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration
public class Application extends RepositoryRestMvcConfiguration {
	
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 1000;
	
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;
	
	@Autowired
	TerminalPatchValidator terminalPatchValidator;
	
	@Autowired
	TerminalDeleteValidator deleteValidator;
	
	@Autowired
	TerminalSaveValidator terminalSaveValidator;
	
	@Autowired
	TerminalAddValidator terminalValidator;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dateFormat);
	}
	
	@Override
	protected void configureValidatingRepositoryEventListener(
			ValidatingRepositoryEventListener validatingListener) {
		validatingListener.addValidator("beforeCreate", terminalValidator);
		validatingListener.addValidator("beforeDelete", deleteValidator);
		validatingListener.addValidator("beforeSave", terminalPatchValidator);
		validatingListener.addValidator("afterCreate", terminalSaveValidator);
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
