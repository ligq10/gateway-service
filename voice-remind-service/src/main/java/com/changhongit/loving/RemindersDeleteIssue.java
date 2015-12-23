package com.changhongit.loving;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.ReminderTerminal;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.model.DeleteVoiceReminder;
import com.changhongit.loving.repository.ReminderRepository;
import com.changhongit.loving.repository.ReminderTerminalRepository;
import com.changhongit.loving.repository.TerminalRepository;

@Component
public class RemindersDeleteIssue implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private ReminderRepository reminderRepository;
	
	@Autowired
	private ReminderTerminalRepository reminderTerminalRepository;
	
	@Override
	public void run() {
		List<ReminderTerminal> reminderTerminals = reminderTerminalRepository
				.findByStatus(false);
		Iterator<ReminderTerminal> iterator = reminderTerminals.iterator();
		
		while (iterator.hasNext()) {
			ReminderTerminal reminderTimenal = iterator.next();
			Terminal terminal = terminalRepository.findOne(reminderTimenal
					.getTerminalId());
			DeleteVoiceReminder deleteVoiceReminder = new DeleteVoiceReminder();
			deleteVoiceReminder.setImei(terminal.getImei());
			deleteVoiceReminder.setMessage(9);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DeleteVoiceReminder> request = new HttpEntity<DeleteVoiceReminder>(
					deleteVoiceReminder, headers);
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			try {
				ResponseEntity<String> exchange = restTemplate.exchange(
						env.getProperty("reminderdelete.issue"),
						HttpMethod.POST, request, String.class);
				reminderTerminalRepository.delete(reminderTimenal);
			} catch (RestClientException e) {
				logger.error("Error while visiting {}", e);
			}
		}
		
	}
}
