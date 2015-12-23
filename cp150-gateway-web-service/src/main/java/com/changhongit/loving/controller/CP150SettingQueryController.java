package com.changhongit.loving.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.document.Cp150Setting;
import com.changhongit.loving.document.Cp150VoiceReminder;
import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.SOSSetting;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.jpaRepository.GroupRepository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.ContactForApp;
import com.changhongit.loving.model.ProtectedCircle;
import com.changhongit.loving.model.ProtectedCircleResponse;
import com.changhongit.loving.model.VoiceReminderV2;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.changhongit.loving.repository.Cp150VoiceReminderRepository;
import com.changhongit.loving.resource.Cp150SettingResource;
import com.changhongit.loving.resource.Cp150SettingsResource;
import com.changhongit.loving.resource.Cp150VoiceRemindersAppResource;
import com.changhongit.loving.resource.Cp150VoiceRemindersResource;

@Controller
public class CP150SettingQueryController {
	
	private static final String SETTINGS = "/settings";
	
	private static final String SETTINGS_CONTACTS = "contacts";
	
	private static final String SETTINGS_CONTACTS_APP = "contactsforapp";
	
	private static final String WHITE_LIST = "whitelist";
	
	private static final String WHITE_LIST_APP = "whitelistforapp";
	
	private static final String INITIALIZATION = "initialization";
	
	private static final String PROTECTED_CIRCLE = "protectedcircle";
	
	private static final String ANSWER = "answer";
	
	private static final String SERVER_CONF = "serverconf";
	
	private static final String AUTO_ANSWER = "autoanswer";
	
	private static final String PEDOMETER_INTERVAL = "pedometerinterval";
	
	private static final String HEARTBEAT_INTERVAL = "heartbeatinterval";
	
	private static final String SOS = "sos";
	
	private static final String AREA = "area";
	
	private static final String PASSWORD = "password";
	
	private static final String GPS_POWER = "gpspower";
	
	private static final String IDLE_WARNING = "idlewarning";
	
	private static final String VOICE_REMINDERS = "/voicereminders";
	
	private static final String VOICE_REMINDERS_APP = "/voiceremindersforapp";
	
	private static final String HAL_JSON = "application/hal+json";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	@Autowired
	private Cp150SettingRepository settingRepository;
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private Cp150VoiceReminderRepository voiceReminderRepository;
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getSettings(
			@PathVariable String imei, HttpServletRequest request) {
		
		Cp150SettingsResource cp150SettingsResource = new Cp150SettingsResource();
		cp150SettingsResource.setImei(imei);
		cp150SettingsResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		cp150SettingsResource.add(new Link(getUri(request, SETTINGS_CONTACTS),
				Cp100SettingKey.CONTACT_LIST));
		cp150SettingsResource.add(new Link(getUri(request, WHITE_LIST),
				Cp100SettingKey.WHITE_LIST));
		cp150SettingsResource.add(new Link(getUri(request, INITIALIZATION),
				Cp100SettingKey.INITIALIZATION));
		cp150SettingsResource.add(new Link(getUri(request, PROTECTED_CIRCLE),
				Cp100SettingKey.PROTECTED_CIRCLE));
		cp150SettingsResource.add(new Link(getUri(request, ANSWER),
				Cp100SettingKey.ANSWER));
		cp150SettingsResource.add(new Link(getUri(request, SERVER_CONF),
				Cp100SettingKey.SERVER_CONF));
		cp150SettingsResource.add(new Link(getUri(request, AUTO_ANSWER),
				Cp100SettingKey.AUTO_ANSWER));
		cp150SettingsResource.add(new Link(getUri(request, PEDOMETER_INTERVAL),
				Cp100SettingKey.PEDOMETER_INTERVAL));
		cp150SettingsResource.add(new Link(getUri(request, HEARTBEAT_INTERVAL),
				Cp100SettingKey.HEARTBEAT_INTERVAL));
		cp150SettingsResource.add(new Link(getUri(request, SOS),
				Cp100SettingKey.SOS));
		cp150SettingsResource.add(new Link(getUri(request, AREA),
				Cp100SettingKey.AREA));
		cp150SettingsResource.add(new Link(getUri(request, PASSWORD),
				Cp100SettingKey.PASSWORD));
		cp150SettingsResource.add(new Link(getUri(request, GPS_POWER),
				Cp100SettingKey.GPS_POWER));
		cp150SettingsResource.add(new Link(getUri(request, IDLE_WARNING),
				Cp100SettingKey.IDLE_WARNING));
		return new ResponseEntity(cp150SettingsResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/"
			+ SETTINGS_CONTACTS_APP, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getContactsForApp(
			@PathVariable String imei, HttpServletRequest request) {
		
		Cp150Setting setting = settingRepository.findByImeiAndKey(imei,
				Cp100SettingKey.CONTACT_LIST);
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		HashMap<String, Contact> contactMap = (HashMap<String, Contact>) setting
				.getSetting();
		List<ContactForApp> contactForApps = new ArrayList<>();
		for (Entry<String, Contact> contactEntry : contactMap.entrySet()) {
			ContactForApp contactForApp = new ContactForApp();
			BeanUtils.copyProperties(contactEntry.getValue(), contactForApp);
			contactForApp.setKeyNum(contactEntry.getKey());
			contactForApps.add(contactForApp);
		}
		Cp150Setting<List<ContactForApp>> settingForApp = new Cp150Setting<List<ContactForApp>>();
		BeanUtils.copyProperties(setting, settingForApp);
		settingForApp.setSetting(contactForApps);
		Cp150SettingResource cp150SettingResource = new Cp150SettingResource(
				settingForApp);
		cp150SettingResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(cp150SettingResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/"
			+ SETTINGS_CONTACTS, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getContacts(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.CONTACT_LIST);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + WHITE_LIST_APP, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getWhiteListForApp(
			@PathVariable String imei, HttpServletRequest request) {
		Cp150Setting setting = settingRepository.findByImeiAndKey(imei,
				Cp100SettingKey.WHITE_LIST);
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		HashMap<String, Contact> contactMap = (HashMap<String, Contact>) setting
				.getSetting();
		List<ContactForApp> contactForApps = new ArrayList<>();
		for (Entry<String, Contact> contactEntry : contactMap.entrySet()) {
			ContactForApp contactForApp = new ContactForApp();
			BeanUtils.copyProperties(contactEntry.getValue(), contactForApp);
			contactForApp.setKeyNum(contactEntry.getKey());
			contactForApps.add(contactForApp);
		}
		Cp150Setting<List<ContactForApp>> settingForApp = new Cp150Setting<List<ContactForApp>>();
		BeanUtils.copyProperties(setting, settingForApp);
		settingForApp.setSetting(contactForApps);
		Cp150SettingResource cp150SettingResource = new Cp150SettingResource(
				settingForApp);
		cp150SettingResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(cp150SettingResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + WHITE_LIST, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getWhiteList(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.WHITE_LIST);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + INITIALIZATION, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getInitialization(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.INITIALIZATION);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/"
			+ PROTECTED_CIRCLE, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getProtectedCircle(
			@PathVariable String imei, HttpServletRequest request) {
		Cp150Setting setting = settingRepository.findByImeiAndKey(imei,
				Cp100SettingKey.PROTECTED_CIRCLE);
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			Cp150Setting contactSetting = settingRepository.findByImeiAndKey(
					imei, Cp100SettingKey.CONTACT_LIST);
			Map<String, Contact> contacts = new HashMap<String, Contact>();
			if (contactSetting != null && contactSetting.getSetting() != null) {
				contacts = (Map<String, Contact>) contactSetting.getSetting();
			}
			Map<String, ProtectedCircleResponse> circleResponses = new HashMap<>();
			Map<String, ProtectedCircle> circles = (Map<String, ProtectedCircle>) setting
					.getSetting();
			for (Entry<String, ProtectedCircle> entry : circles.entrySet()) {
				ProtectedCircleResponse circleResponse = new ProtectedCircleResponse();
				BeanUtils.copyProperties(entry.getValue(), circleResponse);
				String contactName = "第"
						+ (entry.getValue().getContactFlag() + 1) + "联系人";
				Contact contact = contacts.get(String.valueOf(entry.getValue()
						.getContactFlag()));
				if (contact != null) {
					if (!StringUtils.isEmpty(contact.getName())) {
						contactName = contact.getName();
					}
				}
				circleResponse.setContactName(contactName);
				circleResponses.put(entry.getKey(), circleResponse);
			}
			setting.setSetting(circleResponses);
		}
		
		Cp150SettingResource cp150SettingResource = new Cp150SettingResource(
				setting);
		cp150SettingResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(cp150SettingResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + ANSWER, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getAnswer(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.ANSWER);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + SERVER_CONF, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getServerConf(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.SERVER_CONF);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + AUTO_ANSWER, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getAutoAnswer(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.AUTO_ANSWER);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/"
			+ PEDOMETER_INTERVAL, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getPedometerInterval(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei,
				Cp100SettingKey.PEDOMETER_INTERVAL);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/"
			+ HEARTBEAT_INTERVAL, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getHeartbeatInterval(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei,
				Cp100SettingKey.HEARTBEAT_INTERVAL);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + SOS, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getSos(
			@PathVariable String imei, HttpServletRequest request) {
		Cp150Setting setting = settingRepository.findByImeiAndKey(imei,
				Cp100SettingKey.SOS);
		if (setting == null) {
			Terminal terminal = terminalRepository.findByImei(imei);
			if (terminal != null) {
				Group group = groupRepository.findOne(terminal.getGroupId());
				if (group != null) {
					setting = new Cp150Setting<>();
					setting.setImei(imei);
					setting.setKey(Cp100SettingKey.SOS);
					if (group.getSosSetting() != null) {
						setting.setSetting(group.getSosSetting());
					} else {
						setting.setSetting(new SOSSetting());
					}
					
				}
			}
			
		}
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		Cp150SettingResource cp150SettingResource = new Cp150SettingResource(
				setting);
		cp150SettingResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(cp150SettingResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + AREA, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getArea(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.AREA);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + PASSWORD, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getPassword(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.PASSWORD);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + GPS_POWER, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getGpsPower(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.GPS_POWER);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + SETTINGS + "/" + IDLE_WARNING, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getIdelWarning(
			@PathVariable String imei, HttpServletRequest request) {
		
		return getSettingResponse(request, imei, Cp100SettingKey.IDLE_WARNING);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + VOICE_REMINDERS_APP, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getVoiceRemindersForApp(
			@PathVariable String imei, HttpServletRequest request) {
		
		Cp150VoiceReminder cp150VoiceReminder = voiceReminderRepository
				.findByImei(imei);
		if (cp150VoiceReminder == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		HashMap<String, VoiceReminderV2> contactMap = (HashMap<String, VoiceReminderV2>) cp150VoiceReminder
				.getReminders();
		List<VoiceReminderV2> voiceReminderV2s = new ArrayList<>();
		for (Entry<String, VoiceReminderV2> entry : contactMap.entrySet()) {
			voiceReminderV2s.add(entry.getValue());
		}
		
		Cp150VoiceRemindersAppResource voiceRemindersResource = new Cp150VoiceRemindersAppResource();
		voiceRemindersResource.setImei(imei);
		voiceRemindersResource.setLastUpdated(cp150VoiceReminder
				.getLastUpdated());
		voiceRemindersResource.setVoiceReminders(voiceReminderV2s);
		voiceRemindersResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(voiceRemindersResource, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/cp150s/{imei}" + VOICE_REMINDERS, produces = HAL_JSON)
	public ResponseEntity<Cp150SettingResource> getVoiceReminders(
			@PathVariable String imei, HttpServletRequest request) {
		
		Cp150VoiceReminder cp150VoiceReminder = voiceReminderRepository
				.findByImei(imei);
		if (cp150VoiceReminder == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		Cp150VoiceRemindersResource voiceRemindersResource = new Cp150VoiceRemindersResource(
				cp150VoiceReminder);
		voiceRemindersResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(voiceRemindersResource, HttpStatus.OK);
		
	}
	
	private ResponseEntity<Cp150SettingResource> getSettingResponse(
			HttpServletRequest request, String imei, String settingKey) {
		Cp150Setting setting = settingRepository.findByImeiAndKey(imei,
				settingKey);
		if (setting == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		Cp150SettingResource cp150SettingResource = new Cp150SettingResource(
				setting);
		cp150SettingResource.add(new Link(getHost(request)
				+ request.getRequestURI(), Link.REL_SELF));
		return new ResponseEntity(cp150SettingResource, HttpStatus.OK);
	}
	
	private String getHost(HttpServletRequest request) {
		int port = request.getServerPort();
		String host = request.getServerName();
		String header = request.getHeader("X-Forwarded-Host");
		if (StringUtils.hasText(header)) {
			return "http://" + header;
		}
		return "http://" + host + ":" + port;
	}
	
	private String getUri(HttpServletRequest request, String setting) {
		if (request.getRequestURI().endsWith("/")) {
			return getHost(request) + request.getRequestURI() + setting;
		}
		return getHost(request) + request.getRequestURI() + "/" + setting;
	}
	
}
