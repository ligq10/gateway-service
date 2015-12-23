package com.changhongit.loving.messageQueue;

import java.text.SimpleDateFormat;
import java.util.Date;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.HeartBeat;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalStatus;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.jpaRepository.TerminalStatusRepository;
import com.changhongit.loving.model.Cell;
import com.changhongit.loving.model.SearchGeolocateRequest;
import com.changhongit.loving.model.SearchGeolocateResponse;

public class HeartBeatKafkaConsumer implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	private KafkaStream m_stream;
	
	private MessagePack messagePack;
	
	private Environment env;
	
	private TerminalStatusRepository terminalStatusRepository;
	
	private TerminalRepository terminalRepository;
	
	private RestTemplate restTemplate;
	
	public HeartBeatKafkaConsumer(KafkaStream a_stream, MessagePack messagePack) {
		this.m_stream = a_stream;
		this.messagePack = messagePack;
	}
	
	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		while (it.hasNext()) {
			try {
				byte[] body = it.next().message();
				HeartBeat heartBeat = messagePack.read(body, HeartBeat.class);
				TerminalStatus terminalStatus = terminalStatusRepository
						.findOne(heartBeat.getImei());
				if (terminalStatus == null) {
					terminalStatus = new TerminalStatus();
				}
				heartBeat = setLocation(heartBeat);
				BeanUtils.copyProperties(heartBeat, terminalStatus);
				if (terminalStatus.getGroupUuid() == null
						|| System.currentTimeMillis()
								- terminalStatus.getGroupUuidUpdateTime()
										.getTime() > 1200) {
					Terminal terminal = terminalRepository.findByImei(heartBeat
							.getImei());
					if (terminal != null) {
						terminalStatus.setGroupUuid(terminal.getGroupId());
						terminalStatus.setGroupUuidUpdateTime(new Date());
						terminalStatus.setExpireDate(terminal.getExpireDate());
					}
				}
				
				terminalStatusRepository.save(terminalStatus);
				
			} catch (Exception e) {
				logger.error("Error:", e);
			}
		}
	}
	
	public void setTerminalStatusRepository(
			TerminalStatusRepository terminalStatusRepository) {
		this.terminalStatusRepository = terminalStatusRepository;
	}
	
	public void setTerminalRepository(TerminalRepository terminalRepository) {
		this.terminalRepository = terminalRepository;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	private HeartBeat setLocation(HeartBeat heartBeat) {
		if (heartBeat.getGpsStatus() != 1) {
			
			try {
				Cell cell = new Cell();
				cell.setRadioType("");
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
				heartBeat.setLatitude(Float.valueOf(responseEntity.getBody()
						.getLocation().getLat()));
				heartBeat.setLongitude(Float.valueOf(responseEntity.getBody()
						.getLocation().getLon()));
			} catch (Exception e) {
				logger.error("Error:", e);
				return heartBeat;
			}
		}
		return heartBeat;
	}
	
}
