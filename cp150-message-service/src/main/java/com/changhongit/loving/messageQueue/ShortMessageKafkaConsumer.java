package com.changhongit.loving.messageQueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.Member;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.document.Cp150Setting;
import com.changhongit.loving.entity.HeartBeat;
import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.jpaRepository.HeartbeatRepository;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.SendShortMessage;
import com.changhongit.loving.message.SettingAnswer;
import com.changhongit.loving.message.SettingAutoAnswer;
import com.changhongit.loving.message.SettingContactList;
import com.changhongit.loving.message.SettingInitialization;
import com.changhongit.loving.message.SettingPassword;
import com.changhongit.loving.message.SettingProtectedCircle;
import com.changhongit.loving.message.SettingServerConf;
import com.changhongit.loving.model.BaiduSearchGeolocateResponse;
import com.changhongit.loving.model.Cell;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.ProtectedCircle;
import com.changhongit.loving.model.SearchGeolocateRequest;
import com.changhongit.loving.model.SearchGeolocateResponse;
import com.changhongit.loving.model.SettingContact;
import com.changhongit.loving.model.ShortMessage;
import com.changhongit.loving.model.ShortMessageSetting;
import com.changhongit.loving.model.SimpleParam;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShortMessageKafkaConsumer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private KafkaStream m_stream;
	
	private MessagePack messagePack;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private Environment env;
	
	private Cp150SettingRepository cp150SettingRepository;
	
	private TerminalRepository terminalRestRepository;
	
	private RestTemplate restTemplate;
	
	private TerminalUserRepository terminalUserRepository;
	
	private MessagesToCp150Repository messagesToCp150Repository;
	
	private HeartbeatRepository heartbeatRepository;
	
	private SettingMessageConventer settingMessageConventer;
	
	private Cluster cluster;
	
	private static final String NOT_HAVE_AUTHORITY = "您没有更改设置的权限";
	
	private static final String SETTING_CODE = "1#: 短信位置回报 .2#: 彩信位置回报 .7*x#: 开启防护圈 .8#: 关闭防护圈 .100#: 密码查询 .101#: 号码查询 .102#: 命令集查询 .103#: 当前状态 .200* xxxx #: 密码变更 .201*xxxx#: 第一联络人电话设置 .202*1zzzz#: 第二联络人电话设置 .203*1zzzz#: 第三联络人电话设置 .204*1zzzz#: 第四联络人电话设置 .208*x#:接听来电设置 .209*x#: 自动接听设置";
	
	public ShortMessageKafkaConsumer(KafkaStream m_stream,
			MessagePack messagePack) {
		this.m_stream = m_stream;
		this.messagePack = messagePack;
	}
	
	@Override
	public void run() {
		
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		
		while (it.hasNext()) {
			try {
				byte[] body = it.next().message();
				ShortMessageSetting message = messagePack.read(body,
						ShortMessageSetting.class);
				
				String imei = message.getImei();
				String code = message.getMsgContent().split(",")[2];
				String sentFrom = message.getSentFrom();
				Cp150Setting<HashMap<String, Contact>> contactList = cp150SettingRepository
						.findByImeiAndKey(imei, Cp100SettingKey.CONTACT_LIST);
				if (code.equals("1#") || code.equals("2#")) {
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					String date = dateFormat.format(message.getDate());
					String userName = "";
					TerminalUser terminalUser = terminalUserRepository
							.findByTerminalImei(imei);
					if (terminalUser != null) {
						userName = terminalUser.getRealName();
					}
					String content = String.format("%s%s%s", date, userName,
							getFormattedAddress(imei));
					sendShortMessage(imei, sentFrom, content);
					
				} else if (Pattern.matches("7\\*[0-9]*\\.{0,1}[0-9]*#", code)) {
					Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList = cp150SettingRepository
							.findByImeiAndKey(imei,
									Cp100SettingKey.PROTECTED_CIRCLE);
					for (Entry<String, Contact> entry : contactList
							.getSetting().entrySet()) {
						if (entry.getValue().getTelNum().equals(sentFrom)) {
							settingProtectedCircle(imei, code,
									protectedCircleList, entry);
							String content = String.format("电子围栏已开启，防护圈半径%s公里",
									code);
							sendShortMessage(imei, sentFrom, content);
						}
						cp150SettingRepository.save(protectedCircleList);
					}
					
				} else if (code.equals("8#")) {
					Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList = cp150SettingRepository
							.findByImeiAndKey(imei,
									Cp100SettingKey.PROTECTED_CIRCLE);
					for (Entry<String, Contact> entry : contactList
							.getSetting().entrySet()) {
						if (entry.getValue().getTelNum().equals(sentFrom)) {
							cancelProtectedCircle(imei, protectedCircleList,
									entry);
							
							protectedCircleList.getSetting().remove(
									entry.getKey());
							String content = "电子围栏已取消。";
							sendShortMessage(imei, sentFrom, content);
						}
					}
					cp150SettingRepository.save(protectedCircleList);
				} else if (code.equals("100#")) {
					if (isFistContact(sentFrom, contactList)) {
						Cp150Setting<SimpleParam> setting = cp150SettingRepository
								.findByImeiAndKey(imei,
										Cp100SettingKey.PASSWORD);
						if (setting != null) {
							String password = setting.getSetting().getParam();
							sendShortMessage(imei, sentFrom,
									String.format("密码为：%s", password));
						}
						
					} else {
						sendShortMessage(imei, sentFrom, "您没有查询密码的权限");
					}
				} else if (code.equals("101#")) {
					String contactKey = contactKey(sentFrom, contactList);
					if (!StringUtils.isEmpty(contactKey)) {
						
						String contacts = "";
						for (Entry<String, Contact> entry : contactList
								.getSetting().entrySet()) {
							contacts = contacts
									+ String.format(
											"(%s)%s  ",
											Integer.valueOf(entry.getKey()) + 1,
											entry.getValue().getTelNum());
						}
						String content = String.format("联络人电话为:%s", contacts);
						sendShortMessage(imei, sentFrom, content);
					}
				} else if (code.equals("102#")) {
					String contactKey = contactKey(sentFrom, contactList);
					if (!StringUtils.isEmpty(contactKey)) {
						
						String content = SETTING_CODE;
						sendShortMessage(imei, sentFrom, content);
					}
				} else if (code.equals("103#")) {
					String contactKey = contactKey(sentFrom, contactList);
					if (!StringUtils.isEmpty(contactKey)) {
						processSearchStatus(imei, sentFrom, contactKey);
					}
				} else if (Pattern.matches("200\\*[0-9]{4}#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						processPasswordSetting(imei, code, sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, "您没有修改密码的权限");
					}
				} else if (Pattern.matches("201\\*[0-9]{4}#", code)) {
					String password = code.split("[*,#]")[1];
					Cp150Setting<SimpleParam> setting = cp150SettingRepository
							.findByImeiAndKey(imei, Cp100SettingKey.PASSWORD);
					if (setting != null) {
						setting.getSetting().getParam().equals(password);
						String key = "0";
						String title = "一";
						settingContact(imei, sentFrom, contactList, key, title,
								sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
					
				} else if (Pattern.matches("202\\*[0-9]{0,16}#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						String key = "1";
						String title = "二";
						String telNum = code.split("[*,#]")[1];
						settingContact(imei, sentFrom, contactList, key, title,
								telNum);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern.matches("203\\*[0-9]{0,16}#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						String key = "2";
						String title = "三";
						String telNum = code.split("[*,#]")[1];
						settingContact(imei, sentFrom, contactList, key, title,
								telNum);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern.matches("204\\*[0-9]{0,16}#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						String key = "3";
						String title = "四";
						String telNum = code.split("[*,#]")[1];
						settingContact(imei, sentFrom, contactList, key, title,
								telNum);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern.matches("208\\*[1-2]#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						processAnswerSetting(imei, code, sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
					
				} else if (Pattern.matches("209\\*[0-1]#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						processAtouAnswerSetting(imei, code, sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern.matches("1234567890#", code)) {
					if (isFistContact(sentFrom, contactList)) {
						processInitialization(imei, sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern.matches("1\\*[0-9]{1,16}#", code)) {
					if (isFistContact(sentFrom, contactList)) {
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
				} else if (Pattern
						.matches(
								"2\\*[0-9]{1,3}\\*[0-9]{1,3}\\*[0-9]{1,3}\\*[0-9]{1,3}\\*[0-9]{1,4}#",
								code)) {
					if (isFistContact(sentFrom, contactList)) {
						processServerCof(imei, code, sentFrom);
					} else {
						sendShortMessage(imei, sentFrom, NOT_HAVE_AUTHORITY);
					}
					
				}
				
			} catch (Exception e) {
				logger.error("Error:", e);
			}
		}
	}
	
	private String getFormattedAddress(String imei) {
		HeartBeat heartBeat = getLastHeartBeat(imei);
		Float speed = heartBeat.getSpeed();
		try {
			
			String location = "";
			if (heartBeat.getGpsStatus().intValue() == 1) {
				location = String.format("%s,%s", heartBeat.getLatitude(),
						heartBeat.getLongitude());
			} else {
				Cell cell = new Cell();
				cell.setMobileCountryCode(heartBeat.getMcc());
				cell.setMobileNetworkCode(heartBeat.getMnc());
				cell.setLocationAreaCode(heartBeat.getLac());
				cell.setCellId(heartBeat.getCell());
				SearchGeolocateRequest geolocateRequest = new SearchGeolocateRequest();
				geolocateRequest.addCell(cell);
				MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
				headers.add("Content-Type", "application/json");
				HttpEntity<SearchGeolocateRequest> requestEntity = new HttpEntity<SearchGeolocateRequest>(
						geolocateRequest, headers);
				ResponseEntity<SearchGeolocateResponse> responseEntity = restTemplate
						.postForEntity(
								env.getRequiredProperty("geolocate.endpoint"),
								requestEntity, SearchGeolocateResponse.class);
				
				location = String.format("%s,%s", responseEntity.getBody()
						.getLocation().getLat(), responseEntity.getBody()
						.getLocation().getLon());
			}
			String response = restTemplate.getForObject(String.format(
					env.getRequiredProperty("baidu.geocoder.endpoint"),
					location), String.class);
			BaiduSearchGeolocateResponse geolocateResponse = objectMapper
					.readValue(response, BaiduSearchGeolocateResponse.class);
			if (geolocateResponse.getStatus() == 0) {
				return String.format("位于%s%s速度：%s", geolocateResponse
						.getResult().getFormatted_address(), geolocateResponse
						.getResult().getBusiness(), speed);
			}
		} catch (Exception e) {
			return "无法获取你的位置信息";
		}
		return "无法获取你的位置信息";
	}
	
	private void processSearchStatus(String imei, String sentFrom,
			String contactKey) {
		Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList = cp150SettingRepository
				.findByImeiAndKey(imei, Cp100SettingKey.PROTECTED_CIRCLE);
		String protectedCircleType = "关";
		if (protectedCircleList != null) {
			ProtectedCircle protectedCircle = protectedCircleList.getSetting()
					.get(contactKey);
			if (protectedCircle != null) {
				if (protectedCircle.getType() != 0) {
					protectedCircleType = "开";
				}
			}
		}
		
		Cp150Setting<SimpleParam> answerSetting = cp150SettingRepository
				.findByImeiAndKey(imei, Cp100SettingKey.ANSWER);
		
		String answerType = "白名单电话";
		if (answerSetting != null) {
			if (answerSetting.getSetting() != null) {
				if (answerSetting.getSetting().getParam().equals("0")) {
					answerType = "所有电话";
				}
			}
		}
		
		String autoAnswerType = "关";
		Cp150Setting<SimpleParam> autoAnswerSetting = cp150SettingRepository
				.findByImeiAndKey(imei, Cp100SettingKey.AUTO_ANSWER);
		if (autoAnswerSetting != null) {
			if (autoAnswerSetting.getSetting() != null) {
				if (autoAnswerSetting.getSetting().getParam().equals("1")) {
					autoAnswerType = "开";
				}
			}
		}
		
		String cell = "";
		List<HeartBeat> heartBeats = heartbeatRepository
				.findByImeiOrderByDateDesc(imei, new PageRequest(0, 1));
		if (!CollectionUtils.isEmpty(heartBeats)) {
			HeartBeat heartBeat = heartBeats.get(0);
			cell = "" + (Integer.valueOf(heartBeat.getCell()) / 5 * 100);
		}
		
		String version = "";
		Terminal terminal = terminalRestRepository.findByImei(imei);
		if (terminal != null) {
			version = terminal.getVersion();
		}
		
		String content = String.format(
				"防护圈:%s;接听来电:%s;自动接听:%s;定时位置下发:%s;生理数据下发:%s;电池量:%%s;版本号:%s;",
				protectedCircleType, answerType, autoAnswerType, "", "", cell,
				version);
		sendShortMessage(imei, sentFrom, content);
	}
	
	private void processInitialization(String imei, String sentFrom) {
		SettingInitialization settingInitialization = new SettingInitialization();
		settingInitialization.setImei(imei);
		SimpleParam simpleParam = new SimpleParam();
		simpleParam.setParam("1");
		settingInitialization.setImei(imei);
		settingInitialization.setMessage(simpleParam);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingInitialization);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(
				settingInitialization.getImei(), settingMessage));
		sendShortMessage(imei, sentFrom, "系统已经恢复出厂设置。");
	}
	
	private void processPasswordSetting(String imei, String code,
			String sentFrom) {
		String password = code.split("[*,#]")[1];
		SettingPassword settingPassword = new SettingPassword();
		SimpleParam simpleParam = new SimpleParam();
		simpleParam.setParam(password);
		settingPassword.setImei(imei);
		settingPassword.setMessage(simpleParam);
		Cp150Setting<SimpleParam> setting = cp150SettingRepository
				.findByImeiAndKey(imei, Cp100SettingKey.PASSWORD);
		if (setting == null) {
			setting = new Cp150Setting<>();
			setting.setImei(settingPassword.getImei());
			setting.setKey(Cp100SettingKey.PASSWORD);
		}
		setting.setSetting(settingPassword.getMessage());
		setting.setLastUpdated(new Date());
		cp150SettingRepository.save(setting);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingPassword);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(settingPassword.getImei(),
				settingMessage));
		sendShortMessage(imei, sentFrom,
				String.format("密码修改成功，新密码：%s", password));
	}
	
	private void processServerCof(String imei, String code, String sentFrom) {
		String[] parameters = code.split("[*,#]");
		String param = String.format("%s.%s.%s.%s:%s", parameters[1],
				parameters[2], parameters[3], parameters[4], parameters[5]);
		SettingServerConf settingServerConf = new SettingServerConf();
		SimpleParam simpleParam = new SimpleParam();
		simpleParam.setParam(param);
		settingServerConf.setImei(imei);
		settingServerConf.setMessage(simpleParam);
		Cp150Setting<SimpleParam> setting = cp150SettingRepository
				.findByImeiAndKey(settingServerConf.getImei(),
						Cp100SettingKey.SERVER_CONF);
		if (setting == null) {
			setting = new Cp150Setting<>();
			setting.setImei(settingServerConf.getImei());
			setting.setKey(Cp100SettingKey.SERVER_CONF);
		}
		setting.setSetting(settingServerConf.getMessage());
		setting.setLastUpdated(new Date());
		cp150SettingRepository.save(setting);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingServerConf);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(settingServerConf.getImei(),
				settingMessage));
		sendShortMessage(imei, sentFrom, String.format("服务器地址设置为：%s", param));
	}
	
	private void processAtouAnswerSetting(String imei, String code,
			String sentFrom) {
		String param = code.split("[*,#]")[1];
		SettingAutoAnswer settingAutoAnswer = new SettingAutoAnswer();
		SimpleParam simpleParam = new SimpleParam();
		simpleParam.setParam(param);
		settingAutoAnswer.setImei(imei);
		settingAutoAnswer.setMessage(simpleParam);
		Cp150Setting<SimpleParam> setting = cp150SettingRepository
				.findByImeiAndKey(settingAutoAnswer.getImei(),
						Cp100SettingKey.AUTO_ANSWER);
		if (setting == null) {
			setting = new Cp150Setting<>();
			setting.setImei(settingAutoAnswer.getImei());
			setting.setKey(Cp100SettingKey.AUTO_ANSWER);
		}
		setting.setSetting(settingAutoAnswer.getMessage());
		setting.setLastUpdated(new Date());
		cp150SettingRepository.save(setting);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingAutoAnswer);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(settingAutoAnswer.getImei(),
				settingMessage));
		String content = param.equals("1") ? "开启" : "关闭";
		sendShortMessage(imei, sentFrom,
				String.format("自动接听设置已设置为：%s", content));
	}
	
	private void processAnswerSetting(String imei, String code, String sentFrom) {
		String originalParam = code.split("[*,#]")[1];
		String param = originalParam.equals("1") ? "0" : "1";
		SettingAnswer settingAnswer = new SettingAnswer();
		SimpleParam simpleParam = new SimpleParam();
		simpleParam.setParam(param);
		settingAnswer.setImei(imei);
		settingAnswer.setMessage(simpleParam);
		Cp150Setting<SimpleParam> setting = cp150SettingRepository
				.findByImeiAndKey(settingAnswer.getImei(),
						Cp100SettingKey.ANSWER);
		if (setting == null) {
			setting = new Cp150Setting<>();
			setting.setImei(settingAnswer.getImei());
			setting.setKey(Cp100SettingKey.ANSWER);
		}
		setting.setSetting(settingAnswer.getMessage());
		setting.setLastUpdated(new Date());
		cp150SettingRepository.save(setting);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingAnswer);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(settingAnswer.getImei(),
				settingMessage));
		
		String content = originalParam.equals("1") ? "接听所有人电话" : "接听白名单电话";
		sendShortMessage(imei, sentFrom, String.format("接听设置已设置为：%s", content));
	}
	
	private boolean isFistContact(String sentFrom,
			Cp150Setting<HashMap<String, Contact>> contactList) {
		return contactList.getSetting().get("0").getTelNum().equals(sentFrom);
	}
	
	private String contactKey(String sentFrom,
			Cp150Setting<HashMap<String, Contact>> contactList) {
		for (Entry<String, Contact> entry : contactList.getSetting().entrySet()) {
			if (sentFrom.equals(entry.getValue().getTelNum())) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private void settingContact(String imei, String sentFrom,
			Cp150Setting<HashMap<String, Contact>> contactList, String key,
			String title, String telNum) {
		Contact contact = contactList.getSetting().get(key);
		if (contact == null) {
			contact = new Contact();
		}
		contact.setTelNum(telNum);
		contactList.getSetting().put(key, contact);
		cp150SettingRepository.save(contactList);
		SettingContactList settingContactList = new SettingContactList();
		settingContactList.setImei(imei);
		ArrayList<SettingContact> settingContacts = new ArrayList<>();
		for (Entry<String, Contact> entry : contactList.getSetting().entrySet()) {
			SettingContact settingContact = new SettingContact();
			settingContact.setPosition(Integer.valueOf(entry.getKey()));
			settingContact.setTelNum(entry.getValue().getTelNum());
			settingContact.setName(entry.getValue().getName());
			settingContacts.add(settingContact);
		}
		settingContactList.setMessage(settingContacts);
		String settingMessage = settingMessageConventer
				.getSettingMessage(settingContactList);
		saveMessage(settingMessage);
		sendMessageToCp150(new Cp150DownMessage(imei, settingMessage));
		
		sendShortMessage(imei, sentFrom,
				String.format("您的号码%s已经成功设定为第%s联络人;", telNum, title));
	}
	
	private void settingProtectedCircle(
			String imei,
			String code,
			Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList,
			Entry<String, Contact> entry) {
		ProtectedCircle protectedCircle = new ProtectedCircle();
		protectedCircle.setType(1);
		String radius = code.split("[*,#]")[1];
		protectedCircle.setRadius(radius);
		HeartBeat heartBeat = getLastHeartBeat(imei);
		protectedCircle
				.setCentreLongt(String.valueOf(heartBeat.getLongitude()));
		protectedCircle.setCentreLat(String.valueOf(heartBeat.getLatitude()));
		SettingProtectedCircle settingProtectedCircle = new SettingProtectedCircle();
		settingProtectedCircle.setImei(imei);
		settingProtectedCircle.setMessage(protectedCircle);
		
		String protectedCircleSetting = settingMessageConventer
				.getSettingMessage(settingProtectedCircle);
		saveMessage(protectedCircleSetting);
		sendMessageToCp150(new Cp150DownMessage(imei, protectedCircleSetting));
		protectedCircleList.getSetting().put(Integer.valueOf(entry.getKey()),
				protectedCircle);
	}
	
	private HeartBeat getLastHeartBeat(String imei) {
		List<HeartBeat> heartBeats = heartbeatRepository
				.findByImeiOrderByDateDesc(imei, new PageRequest(0, 1));
		HeartBeat heartBeat = heartBeats.get(0);
		return heartBeat;
	}
	
	private void cancelProtectedCircle(
			String imei,
			Cp150Setting<HashMap<Integer, ProtectedCircle>> protectedCircleList,
			Entry<String, Contact> entry) {
		SettingProtectedCircle cancelProtectedCircle = new SettingProtectedCircle();
		ProtectedCircle protectedCircle = protectedCircleList.getSetting().get(
				entry.getKey());
		protectedCircle.setType(0);
		cancelProtectedCircle.setImei(imei);
		cancelProtectedCircle.setMessage(protectedCircle);
		String cancelProtectedCircleSetting = settingMessageConventer
				.getSettingMessage(cancelProtectedCircle);
		saveMessage(cancelProtectedCircleSetting);
		sendMessageToCp150(new Cp150DownMessage(imei,
				cancelProtectedCircleSetting));
	}
	
	private void sendShortMessage(String imei, String sentFrom, String content) {
		SendShortMessage sendShortMessage = new SendShortMessage();
		sendShortMessage.setImei(imei);
		ShortMessage shortMessage = new ShortMessage();
		shortMessage.setTelNum(sentFrom);
		shortMessage.setContent(content);
		sendShortMessage.setMessage(shortMessage);
		String shortMessageString = settingMessageConventer
				.getSettingMessage(sendShortMessage);
		saveMessage(shortMessageString);
		sendMessageToCp150(new Cp150DownMessage(imei, shortMessageString));
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
	
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public void setCp150SettingRepository(
			Cp150SettingRepository cp150SettingRepository) {
		this.cp150SettingRepository = cp150SettingRepository;
	}
	
	public void setTerminalRestRepository(
			TerminalRepository terminalRestRepository) {
		this.terminalRestRepository = terminalRestRepository;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public void setTerminalUserRepository(
			TerminalUserRepository terminalUserRepository) {
		this.terminalUserRepository = terminalUserRepository;
	}
	
	public void setMessagesToCp150Repository(
			MessagesToCp150Repository messagesToCp150Repository) {
		this.messagesToCp150Repository = messagesToCp150Repository;
	}
	
	public void setHeartbeatRepository(HeartbeatRepository heartbeatRepository) {
		this.heartbeatRepository = heartbeatRepository;
	}
	
	public void setSettingMessageConventer(
			SettingMessageConventer settingMessageConventer) {
		this.settingMessageConventer = settingMessageConventer;
	}
	
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
}
