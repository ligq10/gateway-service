package com.changhongit.loving.messageQueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.Member;

import com.changhongit.loving.Cp100SettingKey;
import com.changhongit.loving.KafkaProducerConfiguration;
import com.changhongit.loving.SchemaConfiguration;
import com.changhongit.loving.SettingMessageConventer;
import com.changhongit.loving.document.Cp150Setting;
import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.MessagesToCp150;
import com.changhongit.loving.entity.SOSSetting;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalStatus;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.entity.WarningDetail;
import com.changhongit.loving.jpaRepository.GroupRepository;
import com.changhongit.loving.jpaRepository.MessagesToCp150Repository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalStatusRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;
import com.changhongit.loving.jpaRepository.WarningDetailRepository;
import com.changhongit.loving.message.Cp150DownMessage;
import com.changhongit.loving.message.DialingCall;
import com.changhongit.loving.message.SendShortMessage;
import com.changhongit.loving.model.BaiduSearchGeolocateResponse;
import com.changhongit.loving.model.Call;
import com.changhongit.loving.model.Cell;
import com.changhongit.loving.model.Contact;
import com.changhongit.loving.model.SearchGeolocateRequest;
import com.changhongit.loving.model.SearchGeolocateResponse;
import com.changhongit.loving.model.ShortMessage;
import com.changhongit.loving.model.Warning;
import com.changhongit.loving.repository.Cp150SettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WarningKafkaConsumer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private KafkaStream m_stream;
	
	private MessagePack messagePack;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private RestTemplate restTemplate;
	
	private Environment env;
	
	private Cp150SettingRepository cp150SettingRepository;
	
	private TerminalRepository terminalRestRepository;
	
	private WarningDetailRepository warningDetailRepository;
	
	private TerminalUserRepository terminalUserRepository;
	
	private TerminalStatusRepository terminalStatusRepository;
	
	private MessagesToCp150Repository messagesToCp150Repository;
	
	private KafkaProducerConfiguration kafkaProducerConfiguration;
	
	private SchemaConfiguration schemaConfiguration;
	
	private SettingMessageConventer settingMessageConventer;
	
	private Cluster cluster;
	
	private GroupRepository groupRestRepository;
	
	private static final String WARNING_DETAIL = "warningdetail.kafka.topic";
	
	private static final String SOS = "SOS";
	
	private static final String SOS_DES = "SOS报警";
	
	private static final String LOW_BATTERY = "LOW_BATTERY";
	
	private static final String LOW_BATTERY_DES = "低电报警";
	
	private static final String PROTECTED_CIRCLE_CONTACT_IN = "PROTECTED_CIRCLE_CONTACT_IN";
	
	private static final String PROTECTED_CIRCLE_CONTACT_OUT = "PROTECTED_CIRCLE_CONTACT_OUT";
	
	private static final String PROTECTED_CIRCLE_IN = "注意 :已回到防护圈，请确认";
	
	private static final String PROTECTED_CIRCLE_OUT = "注意 :已离开防护圈 ，请确认";
	
	private static final String PROTECTED_CIRCLE_SUFFIX = "，解除电子围栏请回复8#。";
	
	private static final String CELL_SMPREFIX = "关护通终端电池量低，机器将自动关机，请及时充电。 ";
	
	public WarningKafkaConsumer(KafkaStream m_stream, MessagePack messagePack) {
		this.m_stream = m_stream;
		this.messagePack = messagePack;
	}
	
	@Override
	public void run() {
		
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		
		while (it.hasNext()) {
			try {
				byte[] body = it.next().message();
				Warning warning = messagePack.read(body, Warning.class);
				BigInteger warningFlag = new BigInteger(warning
						.getWarningFlag().substring(2), 16);
				String imei = warning.getImei();
				String userName = "";
				TerminalUser terminalUser = terminalUserRepository
						.findByTerminalImei(imei);
				if (terminalUser != null) {
					userName = terminalUser.getRealName();
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String warningDate = dateFormat.format(warning.getDate());
				long creatTime = warning.getDate().getTime();
				warning = setLocation(warning);
				String location = getFormattedAddress(warning);
				TerminalStatus TerminalStatus = terminalStatusRepository
						.findOne(imei);
				if (TerminalStatus == null) {
					TerminalStatus = new TerminalStatus();
					TerminalStatus.setImei(imei);
				}
				Terminal terminal = terminalRestRepository.findByImei(imei);
				Cp150Setting<HashMap<String, Contact>> contactList = cp150SettingRepository
						.findByImeiAndKey(imei, Cp100SettingKey.CONTACT_LIST);
				Group group = groupRestRepository
						.findOne(terminal.getGroupId());
				Contact contact1 = contactList.getSetting().get("0");
				Contact contact2 = contactList.getSetting().get("1");
				Contact contact3 = contactList.getSetting().get("2");
				Contact contact4 = contactList.getSetting().get("3");
				Contact contactServer = contactList.getSetting().get("4");
				if (hasSosWarning(warningFlag)) {
					WarningDetail sosWarningDetail = new WarningDetail();
					BeanUtils.copyProperties(warning, sosWarningDetail);
					sosWarningDetail.setOwner(userName);
					sosWarningDetail.setOrigin(terminal.getId());
					sosWarningDetail.setType(SOS);
					sosWarningDetail.setContent(SOS_DES);
					sosWarningDetail = warningDetailRepository
							.save(sosWarningDetail);
					kafaSendWarningDetail(sosWarningDetail);
					kafaSendWarningEvent(sosWarningDetail.getId(), creatTime,
							"sos.schema.FileName", "sos.kafka.topic");
					
					SOSSetting sosSetting = group.getSosSetting();
					if (sosSetting == null) {
						sosSetting = new SOSSetting();
					}
					sosWarningProcess(warning, imei, userName, location,
							warningDate, sosSetting, contact1, contact2,
							contact3, contact4);
					TerminalStatus.setSosWarning(true);
					terminalStatusRepository.save(TerminalStatus);
				}
				if (hasCellWarning(warningFlag)) {
					if (!TerminalStatus.isCellWarning()) {
						WarningDetail lowBatteryWarningDetail = new WarningDetail();
						BeanUtils.copyProperties(warning,
								lowBatteryWarningDetail, "id");
						lowBatteryWarningDetail.setOrigin(terminal.getId());
						lowBatteryWarningDetail.setOwner(userName);
						lowBatteryWarningDetail.setType(LOW_BATTERY);
						lowBatteryWarningDetail.setContent(LOW_BATTERY_DES);
						lowBatteryWarningDetail = warningDetailRepository
								.save(lowBatteryWarningDetail);
						kafaSendWarningDetail(lowBatteryWarningDetail);
						kafaSendWarningEvent(lowBatteryWarningDetail.getId(),
								creatTime, "cellwarning.schema.FileName",
								"low.battery.kafka.topic");
						
						cellWarningProcess(warning, imei, userName, location,
								warningDate, contact1, contact2, contact3,
								contact4, contactServer);
						TerminalStatus.setCellWarning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isCellWarning()) {
						TerminalStatus.setCellWarning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				
				if (hasProtectedCircle1Warning(warningFlag)) {
					if (!TerminalStatus.isProtectedCircle1Warning()) {
						protectedCircleOUTkafkaSendEvent(warning, creatTime,
								userName, contact1.getName(), terminal.getId());
						
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_OUT,
										PROTECTED_CIRCLE_SUFFIX), contact1);
						TerminalStatus.setProtectedCircle1Warning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isProtectedCircle1Warning()) {
						protectedCircleInkafkaSendEvent(warning, creatTime,
								userName, contact1.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_IN,
										PROTECTED_CIRCLE_SUFFIX), contact1);
						TerminalStatus.setProtectedCircle1Warning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				
				if (hasProtectedCircle2Warning(warningFlag)) {
					if (!TerminalStatus.isProtectedCircle2Warning()) {
						protectedCircleOUTkafkaSendEvent(warning, creatTime,
								userName, contact2.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_OUT,
										PROTECTED_CIRCLE_SUFFIX), contact2);
						TerminalStatus.setProtectedCircle2Warning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isProtectedCircle2Warning()) {
						protectedCircleInkafkaSendEvent(warning, creatTime,
								userName, contact2.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_IN,
										PROTECTED_CIRCLE_SUFFIX), contact2);
						TerminalStatus.setProtectedCircle2Warning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				if (hasProtectedCircle3Warning(warningFlag)) {
					if (!TerminalStatus.isProtectedCircle3Warning()) {
						protectedCircleOUTkafkaSendEvent(warning, creatTime,
								userName, contact3.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_OUT,
										PROTECTED_CIRCLE_SUFFIX), contact3);
						TerminalStatus.setProtectedCircle3Warning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isProtectedCircle3Warning()) {
						protectedCircleInkafkaSendEvent(warning, creatTime,
								userName, contact3.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_IN,
										PROTECTED_CIRCLE_SUFFIX), contact3);
						TerminalStatus.setProtectedCircle3Warning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				if (hasProtectedCircle4Warning(warningFlag)) {
					if (!TerminalStatus.isProtectedCircle4Warning()) {
						protectedCircleOUTkafkaSendEvent(warning, creatTime,
								userName, contact4.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_OUT,
										PROTECTED_CIRCLE_SUFFIX), contact4);
						TerminalStatus.setProtectedCircle4Warning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isProtectedCircle4Warning()) {
						protectedCircleInkafkaSendEvent(warning, creatTime,
								userName, contact4.getName(), terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_IN,
										PROTECTED_CIRCLE_SUFFIX), contact4);
						TerminalStatus.setProtectedCircle4Warning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				if (hasProtectedCircle5Warning(warningFlag)) {
					if (!TerminalStatus.isProtectedCircle5Warning()) {
						protectedCircleOUTkafkaSendEvent(warning, creatTime,
								userName, contactServer.getName(),
								terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_OUT,
										PROTECTED_CIRCLE_SUFFIX), contactServer);
						TerminalStatus.setProtectedCircle5Warning(true);
						terminalStatusRepository.save(TerminalStatus);
					}
				} else {
					if (TerminalStatus.isProtectedCircle5Warning()) {
						protectedCircleInkafkaSendEvent(warning, creatTime,
								userName, contactServer.getName(),
								terminal.getId());
						tellShortMessage(warning, imei, userName, location,
								warningDate, String.format("%s%s",
										PROTECTED_CIRCLE_IN,
										PROTECTED_CIRCLE_SUFFIX), contactServer);
						TerminalStatus.setProtectedCircle5Warning(false);
						terminalStatusRepository.save(TerminalStatus);
					}
				}
				
			} catch (Exception e) {
				logger.error("Error:", e);
			}
		}
	}
	
	private void protectedCircleInkafkaSendEvent(Warning warninng, long time,
			String userName, String contact, String terminalId) {
		WarningDetail protectedCircleINWarningDetail = new WarningDetail();
		BeanUtils
				.copyProperties(warninng, protectedCircleINWarningDetail, "id");
		protectedCircleINWarningDetail.setOwner(userName);
		protectedCircleINWarningDetail.setOrigin(terminalId);
		protectedCircleINWarningDetail.setType(PROTECTED_CIRCLE_CONTACT_IN);
		protectedCircleINWarningDetail.setContent("进联系人" + contact + "防护圈");
		protectedCircleINWarningDetail = warningDetailRepository
				.save(protectedCircleINWarningDetail);
		
		try {
			kafaSendWarningDetail(protectedCircleINWarningDetail);
			kafaSendWarningEvent(protectedCircleINWarningDetail.getId(), time,
					"protectedcircle.in.FileName",
					"protected.circle.in.kafka.topic");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void protectedCircleOUTkafkaSendEvent(Warning warninng, long time,
			String userName, String contact, String terminalId) {
		WarningDetail protectedCircleOUTWarningDetail = new WarningDetail();
		BeanUtils.copyProperties(warninng, protectedCircleOUTWarningDetail,
				"id");
		protectedCircleOUTWarningDetail.setOrigin(terminalId);
		protectedCircleOUTWarningDetail.setOwner(userName);
		protectedCircleOUTWarningDetail.setType(PROTECTED_CIRCLE_CONTACT_OUT);
		protectedCircleOUTWarningDetail.setContent("出联系人" + contact + "防护圈");
		protectedCircleOUTWarningDetail = warningDetailRepository
				.save(protectedCircleOUTWarningDetail);
		try {
			kafaSendWarningDetail(protectedCircleOUTWarningDetail);
			kafaSendWarningEvent(protectedCircleOUTWarningDetail.getId(), time,
					"protectedcircle.out.schema.FileName",
					"protected.circle.out.kafka.topic");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void kafaSendWarningDetail(WarningDetail detail) throws IOException {
		
		Producer<String, byte[]> producer = new Producer<String, byte[]>(
				kafkaProducerConfiguration.getProducerConfig());
		byte[] messageBytes = messagePack.write(detail);
		KeyedMessage<String, byte[]> keyedMessage = new KeyedMessage<String, byte[]>(
				env.getProperty(WARNING_DETAIL), messageBytes);
		producer.send(keyedMessage);
		producer.close();
	}
	
	private void kafaSendWarningEvent(String id, long time, String avro,
			String kafaTopic) throws IOException {
		Schema sosSchema = schemaConfiguration.getAvroSchema(avro);
		GenericRecord sosGenericRecord = new GenericData.Record(sosSchema);
		sosGenericRecord.put("id", id);
		sosGenericRecord.put("created_timestamp", String.valueOf(time));
		DatumWriter<GenericRecord> writer = new SpecificDatumWriter<GenericRecord>(
				sosSchema);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
		writer.write(sosGenericRecord, encoder);
		encoder.flush();
		out.close();
		Producer<String, byte[]> producer = new Producer<String, byte[]>(
				kafkaProducerConfiguration.getProducerConfig());
		KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(
				env.getProperty(kafaTopic), out.toByteArray());
		producer.send(message);
		producer.close();
	}
	
	private void cellWarningProcess(Warning warninng, String imei,
			String userName, String location, String warningDate,
			Contact contact1, Contact contact2, Contact contact3,
			Contact contact4, Contact contactServer) {
		tellShortMessage(warninng, imei, userName, location, warningDate,
				CELL_SMPREFIX, contact1);
		tellShortMessage(warninng, imei, userName, location, warningDate,
				CELL_SMPREFIX, contact2);
		tellShortMessage(warninng, imei, userName, location, warningDate,
				CELL_SMPREFIX, contact3);
		tellShortMessage(warninng, imei, userName, location, warningDate,
				CELL_SMPREFIX, contact4);
		tellShortMessage(warninng, imei, userName, location, warningDate,
				CELL_SMPREFIX, contactServer);
	}
	
	private void sosWarningProcess(Warning warninng, String imei,
			String userName, String location, String warningDate,
			SOSSetting sosSetting, Contact contact1, Contact contact2,
			Contact contact3, Contact contact4) {
		if (sosSetting.getSendShortMessage().equals("1")) {
			if (sosSetting.getSentToC1().equals("1")) {
				tellShortMessage(warninng, imei, userName, location,
						warningDate, sosSetting.getSmPrefix(), contact1);
			}
			if (sosSetting.getSentToC2().equals("1")) {
				tellShortMessage(warninng, imei, userName, location,
						warningDate, sosSetting.getSmPrefix(), contact2);
			}
			if (sosSetting.getSentToC3().equals("1")) {
				tellShortMessage(warninng, imei, userName, location,
						warningDate, sosSetting.getSmPrefix(), contact3);
			}
			if (sosSetting.getSentToC4().equals("1")) {
				tellShortMessage(warninng, imei, userName, location,
						warningDate, sosSetting.getSmPrefix(), contact4);
			}
			if (!StringUtils.isEmpty(sosSetting.getSentC5Tell())) {
				
				Contact contact5 = new Contact();
				contact5.setTelNum(sosSetting.getSentC5Tell());
				tellShortMessage(warninng, imei, userName, location,
						warningDate, sosSetting.getSmPrefix(), contact5);
			}
		}
		
		if (sosSetting.getCalling().equals("1")) {
			if (sosSetting.getCallC1().equals("1")) {
				tellCalling(warninng, imei, userName, warningDate, contact1);
			}
			if (sosSetting.getCallC2().equals("1")) {
				tellCalling(warninng, imei, userName, warningDate, contact2);
			}
			if (sosSetting.getCallC3().equals("1")) {
				tellCalling(warninng, imei, userName, warningDate, contact3);
			}
			if (sosSetting.getCallC4().equals("1")) {
				tellCalling(warninng, imei, userName, warningDate, contact4);
			}
			if (!StringUtils.isEmpty(sosSetting.getCallC5Tell())) {
				
				Contact contact5 = new Contact();
				contact5.setTelNum(sosSetting.getCallC5Tell());
				tellCalling(warninng, imei, userName, warningDate, contact5);
			}
		}
	}
	
	private boolean hasSosWarning(BigInteger warningFlag) {
		return (warningFlag.testBit(0) && !warningFlag.testBit(8));
	}
	
	private boolean hasCellWarning(BigInteger warningFlag) {
		return (warningFlag.testBit(1) && !warningFlag.testBit(9));
	}
	
	private boolean hasProtectedCircle1Warning(BigInteger warningFlag) {
		return (warningFlag.testBit(2) && !warningFlag.testBit(10));
	}
	
	private boolean hasProtectedCircle2Warning(BigInteger warningFlag) {
		return (warningFlag.testBit(3) && !warningFlag.testBit(11));
	}
	
	private boolean hasProtectedCircle3Warning(BigInteger warningFlag) {
		return (warningFlag.testBit(4) && !warningFlag.testBit(12));
	}
	
	private boolean hasProtectedCircle4Warning(BigInteger warningFlag) {
		return (warningFlag.testBit(5) && !warningFlag.testBit(13));
	}
	
	private boolean hasProtectedCircle5Warning(BigInteger warningFlag) {
		return (warningFlag.testBit(6) && !warningFlag.testBit(14));
	}
	
	private void tellShortMessage(Warning warninng, String imei,
			String userName, String location, String warningDate,
			String smPrefix, Contact contact) {
		if (contact != null) {
			SendShortMessage sendShortMessage = new SendShortMessage();
			sendShortMessage.setImei(imei);
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setTelNum(contact.getTelNum());
			shortMessage.setContent(String.format("%s， %s“%s”%s 速度：%s公里/时",
					warningDate, smPrefix, userName, location,
					warninng.getSpeed()));
			sendShortMessage.setMessage(shortMessage);
			String settingMessage = settingMessageConventer
					.getSettingMessage(sendShortMessage);
			saveMessage(settingMessage);
			sendMessageToCp150(new Cp150DownMessage(imei, settingMessage));
		}
	}
	
	private void tellCalling(Warning warninng, String imei, String userName,
			String warningDate, Contact contact) {
		
		if (contact != null) {
			DialingCall dialingCall = new DialingCall();
			dialingCall.setImei(imei);
			Call call = new Call();
			call.setTelNum(contact.getTelNum());
			call.setType(0);
			dialingCall.setMessage(call);
			String calling = settingMessageConventer
					.getSettingMessage(dialingCall);
			saveMessage(calling);
			sendMessageToCp150(new Cp150DownMessage(imei, calling));
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
	
	private Warning setLocation(Warning warning) {
		if (warning.getGpsStatus() != 1) {
			
			try {
				Cell cell = new Cell();
				cell.setMobileCountryCode(warning.getMcc());
				cell.setMobileNetworkCode(warning.getMnc());
				cell.setLocationAreaCode(warning.getLac());
				cell.setCellId(warning.getCell());
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
				warning.setLatitude(Float.valueOf(responseEntity.getBody()
						.getLocation().getLat()));
				warning.setLongitude(Float.valueOf(responseEntity.getBody()
						.getLocation().getLon()));
			} catch (Exception e) {
				logger.error("Error:", e);
				return warning;
			}
		}
		return warning;
	}
	
	private String getFormattedAddress(Warning warninng) {
		try {
			String location = String.format("%s,%s", warninng.getLatitude(),
					warninng.getLongitude());
			String response = restTemplate.getForObject(String.format(
					env.getRequiredProperty("baidu.geocoder.endpoint"),
					location), String.class);
			BaiduSearchGeolocateResponse geolocateResponse = objectMapper
					.readValue(response, BaiduSearchGeolocateResponse.class);
			if (geolocateResponse.getStatus() == 0) {
				return String.format("%s%s", geolocateResponse.getResult()
						.getFormatted_address(), geolocateResponse.getResult()
						.getBusiness());
			}
		} catch (Exception e) {
			logger.error("Error:", e);
			return "无法获取你的位置信息";
		}
		return "无法获取你的位置信息";
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
	
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
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
	
	public void setWarningDetailRepository(
			WarningDetailRepository warningDetailRepository) {
		this.warningDetailRepository = warningDetailRepository;
	}
	
	public void setTerminalUserRepository(
			TerminalUserRepository terminalUserRepository) {
		this.terminalUserRepository = terminalUserRepository;
	}
	
	public void setTerminalStatusRepository(
			TerminalStatusRepository terminalStatusRepository) {
		this.terminalStatusRepository = terminalStatusRepository;
	}
	
	public void setMessagesToCp150Repository(
			MessagesToCp150Repository messagesToCp150Repository) {
		this.messagesToCp150Repository = messagesToCp150Repository;
	}
	
	public void setKafkaProducerConfiguration(
			KafkaProducerConfiguration kafkaProducerConfiguration) {
		this.kafkaProducerConfiguration = kafkaProducerConfiguration;
	}
	
	public void setSchemaConfiguration(SchemaConfiguration schemaConfiguration) {
		this.schemaConfiguration = schemaConfiguration;
	}
	
	public void setSettingMessageConventer(
			SettingMessageConventer settingMessageConventer) {
		this.settingMessageConventer = settingMessageConventer;
	}
	
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public void setGroupRestRepository(GroupRepository groupRestRepository) {
		this.groupRestRepository = groupRestRepository;
	}
	
}
