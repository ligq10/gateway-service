package com.changhongit.loving.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.TerminalStatus;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.jpaRepository.GroupRepository;
import com.changhongit.loving.jpaRepository.TerminalStatusRepository;
import com.changhongit.loving.jpaRepository.TerminalUserRepository;
import com.changhongit.loving.model.TerminalLatestInfo;

@Controller
public class TerminalStatusContorller {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private long THREE_MINUTES = 60 * 1000 * 3;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private TerminalStatusRepository terminalStatusRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	@RequestMapping(value = "onlineTerminals/byGroupIds/count", method = RequestMethod.GET)
	public ResponseEntity<?> getOnlineTerminalsCountInGroups(
			@RequestParam String groupIds) {
		List<String> groupList = new ArrayList<>();
		String[] split = groupIds.split(",");
		for (int i = 0; i < split.length; i++) {
			groupList.add(split[i].trim());
		}
		Date date = new Date(System.currentTimeMillis() - THREE_MINUTES);
		Page<TerminalStatus> byGroupUuidInAndDateAfter = terminalStatusRepository
				.findByGroupUuidInAndDateAfter(groupList, date,
						new PageRequest(0, 5));
		long count = byGroupUuidInAndDateAfter.getTotalElements();
		Map<String, Object> response = new HashMap<>();
		response.put("count", count);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "onlineTerminals/byImeis", method = RequestMethod.GET)
	public ResponseEntity<?> filterOnlineTerminals(@RequestParam String imeis) {
		List<String> terminalLists = new ArrayList<>();
		String[] split = imeis.split(",");
		for (int i = 0; i < split.length; i++) {
			terminalLists.add(split[i].trim());
		}
		Iterable<TerminalStatus> all = terminalStatusRepository
				.findAll(terminalLists);
		List<String> results = new ArrayList<>();
		for (TerminalStatus terminalStatus : all) {
			if (System.currentTimeMillis() - terminalStatus.getDate().getTime() < THREE_MINUTES) {
				results.add(terminalStatus.getImei());
			}
		}
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
	
	@RequestMapping(value = "terminals/latestInfo")
	public ResponseEntity<?> getTerminalLatestInfos(@RequestParam String imei) {
		
		TerminalStatus terminalStatus = terminalStatusRepository.findOne(imei);
		TerminalUser terminalUser = terminalUserRepository
				.findByTerminalImei(imei);
		
		if (terminalStatus == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		TerminalLatestInfo terminalLatestInfo = new TerminalLatestInfo();
		if (terminalUser != null) {
			terminalLatestInfo.setSim(terminalUser.getTelNum());
		}
		terminalLatestInfo.setBatteryLevel(terminalStatus.getBatteralLevel()
				* 20 + "%");
		terminalLatestInfo.setLocationTime(dateFormat.format(terminalStatus
				.getDate()));
		if (System.currentTimeMillis() - terminalStatus.getDate().getTime() < 3 * 60 * 1000) {
			terminalLatestInfo.setOnLine(true);
		} else {
			terminalLatestInfo.setOnLine(false);
		}
		if (terminalStatus.getExpireDate() != null) {
			terminalLatestInfo.setExpireDate(dateFormat.format(terminalStatus
					.getExpireDate()));
		}
		terminalLatestInfo.setGpsStatus(terminalStatus.getGpsStatus());
		if (terminalStatus.getGpsStatus().equals(1)) {
			terminalLatestInfo.setLongitude(terminalStatus.getLongitude());
			terminalLatestInfo.setLatitude(terminalStatus.getLatitude());
		} else {
			terminalLatestInfo.setCell(terminalStatus.getCell());
			terminalLatestInfo.setLac(terminalStatus.getLac());
			terminalLatestInfo.setMnc(terminalStatus.getMnc());
			terminalLatestInfo.setMcc(terminalStatus.getMcc());
		}
		terminalLatestInfo.setChargeStatus(terminalStatus.getChargeStatus());
		return new ResponseEntity<>(terminalLatestInfo, HttpStatus.OK);
	}
	
	@RequestMapping(value = "warningTerminals/byGroupIds/count")
	public ResponseEntity<?> getWarningTerminalsInGroups(
			@RequestParam String groupIds) {
		
		List<String> groupList = new ArrayList<>();
		String[] split = groupIds.split(",");
		for (int i = 0; i < split.length; i++) {
			groupList.add(split[i].trim());
		}
		Map<String, Object> response = new HashMap<>();
		response.put("count", terminalStatusRepository
				.countByGroupUuidInOrWarningFlageIsTrue(groupList));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "monitorCount/byParentId")
	public ResponseEntity<?> monitorCount(@RequestParam String groupId) {
		List<String> groupIds = new ArrayList<>();
		Group rootGroup = groupRepository.findOne(groupId);
		if (rootGroup == null) {
			return new ResponseEntity<>("Invalid ownerGroupId.",
					HttpStatus.BAD_REQUEST);
		}
		groupIds.add(rootGroup.getId());
		List<Group> childrens = rootGroup.getChildrens();
		if (childrens.size() > 0) {
			addChildrenGroupIds(groupIds, childrens);
		}
		Map<String, Object> response = new HashMap<>();
		Date date = new Date(System.currentTimeMillis() - THREE_MINUTES);
		long total = terminalStatusRepository.countByGroupUuidIn(groupIds);
		long online = terminalStatusRepository.countByGroupUuidInAndDateAfter(
				groupIds, date);
		long offline = total - online;
		response.put("total", total);
		response.put("online", online);
		response.put("offline", offline);
		response.put("warning", terminalStatusRepository
				.countByGroupUuidInOrWarningFlageIsTrue(groupIds));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "monitorCount/bySelfId")
	public ResponseEntity<?> monitorCountBySelfId(@RequestParam String groupId) {
		Group group = groupRepository.findOne(groupId);
		if (group == null) {
			return new ResponseEntity<>("Invalid ownerGroupId.",
					HttpStatus.BAD_REQUEST);
		}
		Map<String, Object> response = new HashMap<>();
		Date date = new Date(System.currentTimeMillis() - THREE_MINUTES);
		long total = terminalStatusRepository.countByGroupUuid(groupId);
		long online = terminalStatusRepository.countByGroupUuidAndDateAfter(
				groupId, date);
		long offline = total - online;
		response.put("total", total);
		response.put("online", online);
		response.put("offline", offline);
		response.put("warning", terminalStatusRepository
				.countByGroupUuidOrWarningFlageIsTrue(groupId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	private void addChildrenGroupIds(List<String> groupIds,
			List<Group> childrens) {
		for (Group group : childrens) {
			groupIds.add(group.getId());
			List<Group> subChildren = group.getChildrens();
			if (subChildren.size() > 0) {
				addChildrenGroupIds(groupIds, subChildren);
			}
		}
	}
}
