package com.changhongit.loving;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.changhongit.loving.entity.Reminder;
import com.changhongit.loving.entity.ReminderTerminal;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.model.AddVoiceReminder;
import com.changhongit.loving.model.ReminderMode;
import com.changhongit.loving.model.VoiceReminderV2;
import com.changhongit.loving.repository.ReminderRepository;
import com.changhongit.loving.repository.ReminderTerminalRepository;
import com.changhongit.loving.repository.TerminalRepository;

@Component
public class RemindersIssue implements Runnable {
	
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
		List<Reminder> reminders = reminderRepository.findByNeedIssue(true);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		for (Reminder reminder : reminders) {
			reminder.setNeedIssue(false);
			reminderRepository.save(reminder);
			String content = reminder.getContent();
			boolean active = reminder.getActive();
			ReminderMode mode = reminder.getMode();
			Date reminderTime = reminder.getReminderTime();
			List<ReminderTerminal> reminderTerminals = reminderTerminalRepository
					.findByReminderId(reminder.getId());
			List<String> terminalIds = new ArrayList<>();
			for (ReminderTerminal reminderTerminal : reminderTerminals) {
				terminalIds.add(reminderTerminal.getTerminalId());
			}
			
			Iterable<Terminal> terminals = terminalRepository
					.findAll(terminalIds);
			Iterator<Terminal> iterator = terminals.iterator();
			while (iterator.hasNext()) {
				Terminal terminal = iterator.next();
				AddVoiceReminder addVoiceReminder = new AddVoiceReminder();
				addVoiceReminder.setImei(terminal.getImei());
				VoiceReminderV2 message = new VoiceReminderV2();
				message.setActive(active);
				message.setContent(content);
				message.setMode(mode);
				message.setIndex(9);
				message.setReminderTime(dateFormat.format(reminderTime));
				addVoiceReminder.setMessage(message);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<AddVoiceReminder> request = new HttpEntity<AddVoiceReminder>(
						addVoiceReminder, headers);
				List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
				messageConverters
						.add(new MappingJackson2HttpMessageConverter());
				try {
					ResponseEntity<String> exchange = restTemplate.exchange(
							env.getProperty("reminder.issue"), HttpMethod.POST,
							request, String.class);
				} catch (RestClientException e) {
					logger.error("Error while visiting {}", e);
				}
			}
		}
		
	}
}
