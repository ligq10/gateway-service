package com.changhongit.loving.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.Member;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.document.Cp150Setting;
import com.changhongit.loving.document.Cp150VoiceReminder;
import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.message.AddVoiceReminder;
import com.changhongit.loving.message.AddWhiteList;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.Cp150Message;
import com.changhongit.loving.message.DeleteVoiceReminder;
import com.changhongit.loving.message.DeleteWhiteList;
import com.changhongit.loving.message.SettingProtectedCircle;
import com.changhongit.loving.message.SettingSingleContact;
import com.changhongit.loving.message.UpdateVoiceReminder;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.ProtectedCircle;
import com.changhongit.loving.model.SettingContact;
import com.changhongit.loving.model.VoiceReminderV2;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.changhongit.loving.repository.Cp150VoiceReminderRepository;
import com.changhongit.loving.validator.SettingValidator;

@RestController
public class Cp150SettingControlerV2 {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private Cp150SettingRepository settingRepository;
	
	@Autowired
	SettingValidator validator;
	
	@Autowired
	private Cp150VoiceReminderRepository voiceReminderRepository;
	
	@Autowired
	private SettingMessageConventer settingMessageConventer;
	
	@Autowired
	private MessagesToCp150Repository messagesToCp150Repository;
	
	@Autowired
	private KafkaProducerConfiguration producerConfiguration;
	
	@Autowired
	private static final String SETTING_KAFKA_TOPIC = "setting.kafka.topic";
	
	@Autowired
	private MessagePack messagePack;
	
	@Autowired
	private Cluster cluster;
	
	@RequestMapping(value = "/cp150s/commands/contactupdate", method = { RequestMethod.POST })
	public ResponseEntity<?> updateContactList(
			@RequestBody SettingSingleContact settingSingleContact,
			BindingResult result) {
		validate(settingSingleContact, result);
		String imei = settingSingleContact.getImei();
		try {
			Cp150Setting<HashMap<String, Contact>> contactList = settingRepository
					.findByImeiAndKey(settingSingleContact.getImei(),
							Cp100SettingKey.CONTACT_LIST);
			if (contactList == null) {
				contactList = new Cp150Setting<>();
				contactList.setImei(imei);
				contactList.setKey(Cp100SettingKey.CONTACT_LIST);
				contactList.setSetting(new LinkedHashMap<String, Contact>());
			}
			contactList.setLastUpdated(new Date());
			SettingContact settingContact = settingSingleContact.getMessage();
			String key = String.valueOf(settingContact.getPosition());
			if (contactList.getSetting().containsKey(key)) {
				if (StringUtils.isEmpty(settingContact.getTelNum())) {
					contactList.getSetting().remove(key);
					Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList = settingRepository
							.findByImeiAndKey(imei,
									Cp100SettingKey.PROTECTED_CIRCLE);
					if (protectedCircleList != null
							&& !CollectionUtils.isEmpty(protectedCircleList
									.getSetting())) {
						
						ProtectedCircle protectedCircle = protectedCircleList
								.getSetting().get(key);
						if (protectedCircle != null) {
							SettingProtectedCircle settingProtectedCircle = new SettingProtectedCircle();
							protectedCircle.setType(0);
							settingProtectedCircle.setImei(imei);
							settingProtectedCircle.setMessage(protectedCircle);
							sendMessageToCp150(settingProtectedCircle);
							protectedCircleList.getSetting().remove(
									String.valueOf(key));
							settingRepository.save(protectedCircleList);
						}
					}
				} else {
					Contact contact = contactList.getSetting().get(key);
					contact.setName(settingContact.getName());
					contact.setTelNum(settingContact.getTelNum());
				}
			} else {
				if (!StringUtils.isEmpty(settingContact.getTelNum())) {
					Contact contact = new Contact(settingContact.getTelNum(),
							settingContact.getName());
					contactList.getSetting().put(
							String.valueOf(settingContact.getPosition()),
							contact);
				}
			}
			settingRepository.save(contactList);
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBytes = messagePack.write(contactList);
			KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(SETTING_KAFKA_TOPIC),
					messageBytes);
			producer.send(message);
			producer.close();
			String settingMessage = settingMessageConventer
					.getSettingMessage(settingSingleContact);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(imei, settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = { "/cp150s/commands/whitelistadd",
			"/cp150s/commands/whitelistupdate" }, method = { RequestMethod.POST })
	public ResponseEntity<?> addWhiteList(
			@RequestBody AddWhiteList addWhiteList, BindingResult result) {
		validate(addWhiteList, result);
		
		try {
			Cp150Setting<HashMap<String, Contact>> contactList = settingRepository
					.findByImeiAndKey(addWhiteList.getImei(),
							Cp100SettingKey.WHITE_LIST);
			if (contactList == null) {
				contactList = new Cp150Setting<>();
				contactList.setImei(addWhiteList.getImei());
				contactList.setKey(Cp100SettingKey.WHITE_LIST);
				contactList.setSetting(new LinkedHashMap<String, Contact>());
			}
			contactList.setLastUpdated(new Date());
			Integer position = addWhiteList.getMessage().getPosition();
			Integer nonUsingPosition = null;
			if (position == null) {
				nonUsingPosition = findNonUsingPosition(50, contactList
						.getSetting().keySet());
				if (nonUsingPosition == null) {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
				Contact contact = new Contact();
				contact.setName(addWhiteList.getMessage().getName());
				contact.setTelNum(addWhiteList.getMessage().getTelNum());
				contactList.getSetting().put(nonUsingPosition.toString(),
						contact);
			} else {
				if (contactList.getSetting().containsKey(position.toString())) {
					contactList.getSetting().get(position.toString())
							.setName(addWhiteList.getMessage().getName());
					contactList.getSetting().get(position.toString())
							.setTelNum(addWhiteList.getMessage().getTelNum());
				}
			}
			
			settingRepository.save(contactList);
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBytes = messagePack.write(contactList);
			KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(SETTING_KAFKA_TOPIC),
					messageBytes);
			producer.send(message);
			producer.close();
			String settingMessage = settingMessageConventer
					.getSettingMessage(addWhiteList);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(addWhiteList.getImei(),
					settingMessage));
			if (position == null) {
				SettingContact settingContact = new SettingContact();
				settingContact.setPosition(nonUsingPosition);
				settingContact.setName(addWhiteList.getMessage().getName());
				settingContact.setTelNum(addWhiteList.getMessage().getTelNum());
				return new ResponseEntity<>(settingContact, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cp150s/commands/whitelistdelete", method = { RequestMethod.POST })
	public ResponseEntity<?> deleteWhiteList(
			@RequestBody DeleteWhiteList deleteWhiteList, BindingResult result) {
		validate(deleteWhiteList, result);
		
		try {
			Cp150Setting<HashMap<String, Contact>> contactList = settingRepository
					.findByImeiAndKey(deleteWhiteList.getImei(),
							Cp100SettingKey.WHITE_LIST);
			if (contactList == null) {
				contactList = new Cp150Setting<>();
				contactList.setImei(deleteWhiteList.getImei());
				contactList.setKey(Cp100SettingKey.WHITE_LIST);
				contactList.setSetting(new LinkedHashMap<String, Contact>());
			}
			contactList.setLastUpdated(new Date());
			String key = deleteWhiteList.getMessage().toString();
			if (contactList.getSetting().containsKey(key)) {
				contactList.getSetting().remove(key);
			}
			settingRepository.save(contactList);
			
			Producer<String, byte[]> producer = new Producer<String, byte[]>(
					producerConfiguration.getProducerConfig());
			byte[] messageBytes = messagePack.write(contactList);
			KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(
					producerConfiguration.getTopic(SETTING_KAFKA_TOPIC),
					messageBytes);
			producer.send(message);
			producer.close();
			String settingMessage = settingMessageConventer
					.getSettingMessage(deleteWhiteList);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(deleteWhiteList.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = { "/cp150s/commands/voicereminderadd" }, method = { RequestMethod.POST })
	public ResponseEntity<?> addVoiceReminder(
			@RequestBody AddVoiceReminder voiceReminder) {
		
		try {
			Cp150VoiceReminder cp150VoiceReminder = voiceReminderRepository
					.findByImei(voiceReminder.getImei());
			
			if (cp150VoiceReminder == null) {
				cp150VoiceReminder = new Cp150VoiceReminder();
				cp150VoiceReminder.setImei(voiceReminder.getImei());
				cp150VoiceReminder
						.setReminders(new LinkedHashMap<String, VoiceReminderV2>());
			}
			cp150VoiceReminder.setLastUpdated(new Date());
			
			Integer newVoiceReminderIndex = findNonUsingPosition(10,
					cp150VoiceReminder.getReminders().keySet());
			voiceReminder.getMessage().setIndex(newVoiceReminderIndex);
			
			cp150VoiceReminder.getReminders().put(
					newVoiceReminderIndex.toString(),
					voiceReminder.getMessage());
			
			voiceReminderRepository.save(cp150VoiceReminder);
			
			String settingMessage = settingMessageConventer
					.getSettingMessage(voiceReminder);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(voiceReminder.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(voiceReminder.getMessage(), HttpStatus.OK);
	}
	
	@RequestMapping(value = { "/cp150s/commands/voicereminderupdate" }, method = { RequestMethod.POST })
	public ResponseEntity<?> updateVoiceReminder(
			@RequestBody UpdateVoiceReminder voiceReminder) {
		
		try {
			Cp150VoiceReminder cp150VoiceReminder = voiceReminderRepository
					.findByImei(voiceReminder.getImei());
			
			if (cp150VoiceReminder == null) {
				cp150VoiceReminder = new Cp150VoiceReminder();
				cp150VoiceReminder.setImei(voiceReminder.getImei());
				cp150VoiceReminder
						.setReminders(new LinkedHashMap<String, VoiceReminderV2>());
			}
			cp150VoiceReminder.setLastUpdated(new Date());
			
			cp150VoiceReminder.getReminders().put(
					voiceReminder.getMessage().getIndex().toString(),
					voiceReminder.getMessage());
			
			voiceReminderRepository.save(cp150VoiceReminder);
			
			String settingMessage = settingMessageConventer
					.getSettingMessage(voiceReminder);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(voiceReminder.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = { "/cp150s/commands/voicereminderdelete" }, method = { RequestMethod.POST })
	public ResponseEntity<?> deleteVoiceReminder(
			@RequestBody DeleteVoiceReminder deleteVoiceReminder) {
		
		try {
			Cp150VoiceReminder cp150VoiceReminder = voiceReminderRepository
					.findByImei(deleteVoiceReminder.getImei());
			
			if (cp150VoiceReminder == null) {
				cp150VoiceReminder = new Cp150VoiceReminder();
				cp150VoiceReminder.setImei(deleteVoiceReminder.getImei());
				cp150VoiceReminder
						.setReminders(new LinkedHashMap<String, VoiceReminderV2>());
			}
			cp150VoiceReminder.setLastUpdated(new Date());
			String key = deleteVoiceReminder.getMessage().toString();
			if (cp150VoiceReminder.getReminders().containsKey(key)) {
				cp150VoiceReminder.getReminders().remove(key);
			}
			
			voiceReminderRepository.save(cp150VoiceReminder);
			String settingMessage = settingMessageConventer
					.getSettingMessage(deleteVoiceReminder);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(
					deleteVoiceReminder.getImei(), settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private void validate(Cp150Message setting, BindingResult result) {
		validator.validate(setting, result);
		if (result.hasErrors()) {
			throw new RepositoryConstraintViolationException(result);
		}
	}
	
	private void saveMessage(String settingMessage) {
		MessagesToCp150 messagesToCp150 = new MessagesToCp150();
		String[] params = settingMessage.split(" ");
		messagesToCp150.setSeq(params[2]);
		messagesToCp150.setImei(params[3]);
		messagesToCp150.setMessage(settingMessage);
		messagesToCp150.setSentDate(new Date());
		messagesToCp150Repository.save(messagesToCp150);
	}
	
	private void sendMessageToCp150(final Object message) {
		
		for (final Member member : cluster.state().getMembers()) {
			if (member.hasRole("node")) {
				final ActorSelection actorSelection = cluster.system()
						.actorSelection(
								member.address().toString() + "/user/"
										+ "cp150Connections");
				actorSelection.tell(message, ActorRef.noSender());
			}
		}
	}
	
	private Integer findNonUsingPosition(Integer max, Iterable usedList) {
		if (!usedList.iterator().hasNext()) {
			return 0;
		}
		for (int i = 0; i < max; i++) {
			boolean found = false;
			for (Object used : usedList) {
				if (Integer.parseInt(used.toString()) == i) {
					found = true;
				}
			}
			if (!found) {
				return i;
			}
		}
		return null;
	}
}