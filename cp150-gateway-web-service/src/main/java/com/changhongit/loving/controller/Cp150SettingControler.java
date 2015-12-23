package com.changhongit.loving.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.support.RepositoryConstraintViolationExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.Member;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.JiaHuaMediaType;
import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.document.Cp150Setting;
import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.Cp150Message;
import com.changhongit.loving.message.DialingCall;
import com.changhongit.loving.message.RealTimeBroadcast;
import com.changhongit.loving.message.SendMultimediaMessage;
import com.changhongit.loving.message.SendShortMessage;
import com.changhongit.loving.message.SettingAnswer;
import com.changhongit.loving.message.SettingArea;
import com.changhongit.loving.message.SettingAutoAnswer;
import com.changhongit.loving.message.SettingContactList;
import com.changhongit.loving.message.SettingGpsPower;
import com.changhongit.loving.message.SettingHeartbeatInterval;
import com.changhongit.loving.message.SettingIdelWarning;
import com.changhongit.loving.message.SettingInitialization;
import com.changhongit.loving.message.SettingPassword;
import com.changhongit.loving.message.SettingPedometerInterval;
import com.changhongit.loving.message.SettingPhysiologicalInformation;
import com.changhongit.loving.message.SettingProtectedCircle;
import com.changhongit.loving.message.SettingServerConf;
import com.changhongit.loving.message.SettingSos;
import com.changhongit.loving.message.SettingWhiteList;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.ProtectedCircle;
import com.changhongit.loving.model.SOSConf;
import com.changhongit.loving.model.SettingContact;
import com.changhongit.loving.model.SimpleParam;
import com.changhongit.loving.model.VoiceReminder;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.changhongit.loving.repository.Cp150VoiceReminderRepository;
import com.changhongit.loving.validator.SettingValidator;

@RestController
public class Cp150SettingControler {
	
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
	private MessagePack messagePack;
	
	private static final String SETTING_KAFKA_TOPIC = "setting.kafka.topic";
	
	@Autowired
	private Cluster cluster;
	
	@RequestMapping(value = "/cp150s/commands/contacts", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_CONTACT_LIST })
	public HttpStatus setContactList(
			@RequestBody SettingContactList settingContacts,
			BindingResult result) {
		validate(settingContacts, result);
		String imei = settingContacts.getImei();
		try {
			Cp150Setting<HashMap<String, Contact>> contactList = settingRepository
					.findByImeiAndKey(settingContacts.getImei(),
							Cp100SettingKey.CONTACT_LIST);
			if (contactList == null) {
				contactList = new Cp150Setting<>();
				contactList.setImei(imei);
				contactList.setKey(Cp100SettingKey.CONTACT_LIST);
				contactList.setSetting(new LinkedHashMap<String, Contact>());
			}
			contactList.setLastUpdated(new Date());
			for (SettingContact settingContact : settingContacts.getMessage()) {
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
								settingProtectedCircle
										.setMessage(protectedCircle);
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
						Contact contact = new Contact(
								settingContact.getTelNum(),
								settingContact.getName());
						contactList.getSetting().put(
								String.valueOf(settingContact.getPosition()),
								contact);
					}
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
					.getSettingMessage(settingContacts);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(imei, settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/whitelist", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_WHITE_LIST })
	public HttpStatus setWhiteList(
			@RequestBody SettingWhiteList settingWhiteList, BindingResult result) {
		validate(settingWhiteList, result);
		
		try {
			Cp150Setting<HashMap<String, Contact>> contactList = settingRepository
					.findByImeiAndKey(settingWhiteList.getImei(),
							Cp100SettingKey.WHITE_LIST);
			if (contactList == null) {
				contactList = new Cp150Setting<HashMap<String, Contact>>();
				contactList.setImei(settingWhiteList.getImei());
				contactList.setKey(Cp100SettingKey.WHITE_LIST);
				contactList.setSetting(new LinkedHashMap<String, Contact>());
			} else {
				contactList.getSetting().clear();
			}
			contactList.setLastUpdated(new Date());
			int index = 0;
			int deleteIndex = settingWhiteList.getMessage().size() - 1;
			
			for (SettingContact settingContact : settingWhiteList.getMessage()) {
				if (!StringUtils.isEmpty(settingContact.getTelNum())) {
					Contact contact = new Contact(settingContact.getTelNum(),
							settingContact.getName());
					contactList.getSetting()
							.put(String.valueOf(index), contact);
					settingContact.setPosition(index);
					index++;
				} else {
					settingContact.setPosition(deleteIndex);
					deleteIndex--;
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
					.getSettingMessage(settingWhiteList);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(settingWhiteList.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/initialization", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_INITIALIZATION })
	public HttpStatus settingInitialization(
			@RequestBody SettingInitialization settingInitialization,
			BindingResult result) {
		validate(settingInitialization, result);
		try {
			Cp150Setting<SimpleParam> initealization = settingRepository
					.findByImeiAndKey(settingInitialization.getImei(),
							Cp100SettingKey.INITIALIZATION);
			if (initealization == null) {
				initealization = new Cp150Setting<>();
				initealization.setImei(settingInitialization.getImei());
				initealization.setKey(Cp100SettingKey.INITIALIZATION);
			}
			initealization.setSetting(settingInitialization.getMessage());
			initealization.setLastUpdated(new Date());
			settingRepository.save(initealization);
			sendMessageToCp150(new Cp150DownMessage(
					settingInitialization.getImei(),
					settingMessageConventer
							.getSettingMessage(settingInitialization)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/protectedcircle", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_PROTECTED_CIRCLE })
	public HttpStatus settingProtectedCircle(
			@RequestBody SettingProtectedCircle settingProtectedCircle) {
		
		try {
			Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList = settingRepository
					.findByImeiAndKey(settingProtectedCircle.getImei(),
							Cp100SettingKey.PROTECTED_CIRCLE);
			if (protectedCircleList == null) {
				protectedCircleList = new Cp150Setting<HashMap<Integer, ProtectedCircle>>();
				protectedCircleList.setImei(settingProtectedCircle.getImei());
				protectedCircleList.setKey(Cp100SettingKey.PROTECTED_CIRCLE);
				protectedCircleList
						.setSetting(new LinkedHashMap<Integer, ProtectedCircle>());
			}
			protectedCircleList.setLastUpdated(new Date());
			if (settingProtectedCircle.getMessage().getType() == 0) {
				protectedCircleList.getSetting().remove(
						String.valueOf(settingProtectedCircle.getMessage()
								.getContactFlag()));
			} else {
				protectedCircleList.getSetting().put(
						settingProtectedCircle.getMessage().getContactFlag(),
						settingProtectedCircle.getMessage());
			}
			settingRepository.save(protectedCircleList);
			String settingMessage = settingMessageConventer
					.getSettingMessage(settingProtectedCircle);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(
					settingProtectedCircle.getImei(), settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/answer", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_ANSWER })
	public HttpStatus settingAnswer(@RequestBody SettingAnswer settingAnswer) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingAnswer.getImei(),
							Cp100SettingKey.ANSWER);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingAnswer.getImei());
				setting.setKey(Cp100SettingKey.ANSWER);
			}
			setting.setSetting(settingAnswer.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			String settingMessage = settingMessageConventer
					.getSettingMessage(settingAnswer);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(settingAnswer.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/serverconf", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_SERVER_CONF })
	public HttpStatus settingServerConf(
			@RequestBody SettingServerConf settingServerConf) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingServerConf.getImei(),
							Cp100SettingKey.SERVER_CONF);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingServerConf.getImei());
				setting.setKey(Cp100SettingKey.SERVER_CONF);
			}
			setting.setSetting(settingServerConf.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(
					settingServerConf.getImei(),
					settingMessageConventer
							.getSettingMessage(settingServerConf)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/autoanswer", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_AUTO_ANSWER })
	public HttpStatus settingAutoAnswer(
			@RequestBody SettingAutoAnswer settingAutoAnswer) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingAutoAnswer.getImei(),
							Cp100SettingKey.AUTO_ANSWER);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingAutoAnswer.getImei());
				setting.setKey(Cp100SettingKey.AUTO_ANSWER);
			}
			setting.setSetting(settingAutoAnswer.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			String settingMessage = settingMessageConventer
					.getSettingMessage(settingAutoAnswer);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(
					settingAutoAnswer.getImei(), settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/pedometerinterval", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_PEDOMETER_INTERVAL })
	public HttpStatus settingPedometerInterval(
			@RequestBody SettingPedometerInterval settingPedometerInterval) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingPedometerInterval.getImei(),
							Cp100SettingKey.PEDOMETER_INTERVAL);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingPedometerInterval.getImei());
				setting.setKey(Cp100SettingKey.PEDOMETER_INTERVAL);
			}
			setting.setSetting(settingPedometerInterval.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(
					settingPedometerInterval.getImei(),
					settingMessageConventer
							.getSettingMessage(settingPedometerInterval)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/heartbeatinterval", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_HEARTBEAT_INTERVAL })
	public HttpStatus settingHeartbeatInterval(
			@RequestBody SettingHeartbeatInterval settingHeartbeatInterval) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingHeartbeatInterval.getImei(),
							Cp100SettingKey.HEARTBEAT_INTERVAL);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingHeartbeatInterval.getImei());
				setting.setKey(Cp100SettingKey.HEARTBEAT_INTERVAL);
			}
			setting.setSetting(settingHeartbeatInterval.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(
					settingHeartbeatInterval.getImei(),
					settingMessageConventer
							.getSettingMessage(settingHeartbeatInterval)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/sos", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_SOS })
	public HttpStatus settingSos(@RequestBody SettingSos settingSos) {
		
		try {
			Cp150Setting<SOSConf> setting = settingRepository.findByImeiAndKey(
					settingSos.getImei(), Cp100SettingKey.SOS);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingSos.getImei());
				setting.setKey(Cp100SettingKey.SOS);
			}
			setting.setSetting(settingSos.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			String settingMessage = settingMessageConventer
					.getSettingMessage(settingSos);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(settingSos.getImei(),
					settingMessage));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/area", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_AREA })
	public HttpStatus settingArea(@RequestBody SettingArea settingArea) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingArea.getImei(),
							Cp100SettingKey.AREA);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingArea.getImei());
				setting.setKey(Cp100SettingKey.AREA);
			}
			setting.setSetting(settingArea.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(settingArea.getImei(),
					settingMessageConventer.getSettingMessage(settingArea)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/password", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_PASSWORD })
	public HttpStatus settingPassword(
			@RequestBody SettingPassword settingPassword) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingPassword.getImei(),
							Cp100SettingKey.PASSWORD);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingPassword.getImei());
				setting.setKey(Cp100SettingKey.PASSWORD);
			}
			setting.setSetting(settingPassword.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(settingPassword.getImei(),
					settingMessageConventer.getSettingMessage(settingPassword)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/gpspower", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_GPS_POWER })
	public HttpStatus settingGpsPower(
			@RequestBody SettingGpsPower settingGpsPower) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingGpsPower.getImei(),
							Cp100SettingKey.GPS_POWER);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingGpsPower.getImei());
				setting.setKey(Cp100SettingKey.GPS_POWER);
			}
			setting.setSetting(settingGpsPower.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(settingGpsPower.getImei(),
					settingMessageConventer.getSettingMessage(settingGpsPower)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/idlewarning", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_IDLE_WARNING })
	public HttpStatus settingIdelWarning(
			@RequestBody SettingIdelWarning settingIdelWarning) {
		
		try {
			Cp150Setting<SimpleParam> setting = settingRepository
					.findByImeiAndKey(settingIdelWarning.getImei(),
							Cp100SettingKey.IDLE_WARNING);
			if (setting == null) {
				setting = new Cp150Setting<>();
				setting.setImei(settingIdelWarning.getImei());
				setting.setKey(Cp100SettingKey.IDLE_WARNING);
			}
			setting.setSetting(settingIdelWarning.getMessage());
			setting.setLastUpdated(new Date());
			settingRepository.save(setting);
			sendMessageToCp150(new Cp150DownMessage(
					settingIdelWarning.getImei(),
					settingMessageConventer
							.getSettingMessage(settingIdelWarning)));
		} catch (Exception e) {
			logger.error("Error: ", e);
			return HttpStatus.BAD_REQUEST;
		}
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/shortmessage", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SHORT_MESSAGE })
	public HttpStatus sendShortMessage(
			@RequestBody SendShortMessage sendShortMessage) {
		
		sendMessageToCp150(new Cp150DownMessage(sendShortMessage.getImei(),
				settingMessageConventer.getSettingMessage(sendShortMessage)));
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/dialingcall", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.DIALING_CALL })
	public HttpStatus dialingCall(@RequestBody DialingCall dialingCall) {
		
		sendMessageToCp150(new Cp150DownMessage(dialingCall.getImei(),
				settingMessageConventer.getSettingMessage(dialingCall)));
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/realtimebroadcast", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.REAL_TIME_BROADCAST })
	public HttpStatus realTimeBroadcast(
			@RequestBody RealTimeBroadcast realTimeBroadcast) {
		
		sendMessageToCp150(new Cp150DownMessage(realTimeBroadcast.getImei(),
				settingMessageConventer.getSettingMessage(realTimeBroadcast)));
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/multimediamessage", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.MULTIMEDIA_MESSAGE })
	public HttpStatus sendMultimediaMessage(
			@RequestBody SendMultimediaMessage sendMultimediaMessage) {
		
		sendMessageToCp150(new Cp150DownMessage(
				sendMultimediaMessage.getImei(),
				settingMessageConventer
						.getSettingMessage(sendMultimediaMessage)));
		
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/cp150s/commands/physiologicalinformation", method = { RequestMethod.POST }, consumes = { JiaHuaMediaType.SETTING_PHYSIOLOGICAL_INFORMATION })
	public HttpStatus settingPhysiologicalInformation(
			@RequestBody SettingPhysiologicalInformation settingPhysiologicalInformation) {
		
		sendMessageToCp150(new Cp150DownMessage(
				settingPhysiologicalInformation.getImei(),
				settingMessageConventer
						.getSettingMessage(settingPhysiologicalInformation)));
		
		return HttpStatus.OK;
	}
	
	@ExceptionHandler({ RepositoryConstraintViolationException.class })
	@ResponseBody
	public ResponseEntity handleRepositoryConstraintViolationException(
			Locale locale, RepositoryConstraintViolationException rcve) {
		return response(null,
				new RepositoryConstraintViolationExceptionMessage(rcve,
						new MessageSourceAccessor(messageSource)),
				HttpStatus.BAD_REQUEST);
	}
	
	public <T> ResponseEntity<T> response(HttpHeaders headers, T body,
			HttpStatus status) {
		HttpHeaders hdrs = new HttpHeaders();
		if (null != headers) {
			hdrs.putAll(headers);
		}
		return new ResponseEntity<T>(body, hdrs, status);
	}
	
	private int choseIndex(Map<String, VoiceReminder> reminders) {
		for (int i = 0; i < 10; i++) {
			if (reminders.containsKey(String.valueOf(i))) {
				continue;
			} else {
				return i;
			}
		}
		return 9;
	}
	
	private int chosePosition(Map<String, Contact> whiteList) {
		for (int i = 0; i < 50; i++) {
			if (whiteList.containsKey(String.valueOf(i))) {
				continue;
			} else {
				return i;
			}
		}
		return 49;
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
}