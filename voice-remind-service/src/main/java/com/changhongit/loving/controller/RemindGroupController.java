package com.changhongit.loving.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.RemindGroup;
import com.changhongit.loving.entity.RemindGroupTerminal;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.model.AddTerminalsToRemindGroupRequest;
import com.changhongit.loving.model.CreateRemindGroupRequest;
import com.changhongit.loving.model.RemindGroupResponse;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.RemindGroupRepository;
import com.changhongit.loving.repository.RemindGroupTerminalRepository;
import com.changhongit.loving.repository.TerminalRepository;
import com.jayway.jsonpath.JsonPath;

@Controller
public class RemindGroupController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private RemindGroupRepository remindGroupRepository;
	
	@Autowired
	private RemindGroupTerminalRepository remindGroupTerminalRepository;
	
	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private Environment env;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@RequestMapping(value = "/remindgroups/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getRemindGroupTerminals(@PathVariable String id,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "50") Integer size) {
		
		RemindGroup remindGroup = remindGroupRepository.findOne(id);
		if (remindGroup == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			PageRequest pageRequest = new PageRequest(page, size);
			final Page<RemindGroupTerminal> byRemindGroupId = remindGroupTerminalRepository
					.findByRemindGroupId(id, pageRequest);
			List<String> terminalIds = new ArrayList<>();
			for (RemindGroupTerminal remindGroupTerminal : byRemindGroupId) {
				terminalIds.add(remindGroupTerminal.getTerminalId());
			}
			
			Iterable<Terminal> terminals = terminalRepository
					.findAll(terminalIds);
			final List<Terminal> content = new ArrayList<>();
			Iterator<Terminal> iterator = terminals.iterator();
			while (iterator.hasNext()) {
				content.add(iterator.next());
			}
			remindGroup.setTerminalCount((int) byRemindGroupId
					.getTotalElements());
			RemindGroupResponse response = new RemindGroupResponse(
					byRemindGroupId, remindGroup, content);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/remindgroups", method = RequestMethod.GET)
	public ResponseEntity<?> getRemindGroups(
			@RequestParam(value = "groupId", required = false, defaultValue = "guanhutong") String parentOwnerGroupId,
			@PageableDefault(page = 0, size = 20) Pageable pageRequest) {
		List<String> groupIdList = new ArrayList<>();
		Group group = groupRepository.findOne(parentOwnerGroupId);
		if (group != null) {
			groupIdList = addList(group, groupIdList);
		}
		if (CollectionUtils.isEmpty(groupIdList)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		Page<RemindGroup> remindGroupPage = remindGroupRepository
				.findByOwnerGroupIdInOrderByCreateTimeDesc(groupIdList,
						pageRequest);
		for (RemindGroup remindgroup : remindGroupPage.getContent()) {
			remindgroup.setTerminalCount(remindGroupTerminalRepository
					.countByRemindGroupId(remindgroup.getId()));
		}
		return new ResponseEntity<>(remindGroupPage, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/remindgroups", method = RequestMethod.POST)
	public ResponseEntity<?> createRemindGroup(
			@RequestBody CreateRemindGroupRequest request) {
		
		RemindGroup remindGroup = new RemindGroup();
		remindGroup.setName(request.getName());
		remindGroup.setOwnerGroupId(request.getOwnerGroupId());
		if (request.getSearchKeywords() != null) {
			String keywords = "";
			for (String keyword : request.getSearchKeywords()) {
				keywords += keyword + " ";
			}
			remindGroup.setSearchKeywords(keywords);
		}
		remindGroupRepository.save(remindGroup);
		
		List<RemindGroupTerminal> remindGroupTerminals = new ArrayList<>();
		if (request.getTerminalIds() != null) {
			Iterable<Terminal> findAll = terminalRepository.findAll(request
					.getTerminalIds());
			Iterator<Terminal> iterator = findAll.iterator();
			while (iterator.hasNext()) {
				RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
				remindGroupTerminal.setRemindGroupId(remindGroup.getId());
				remindGroupTerminal.setTerminalId(iterator.next().getId());
				remindGroupTerminals.add(remindGroupTerminal);
			}
		}
		if (request.getGroupIds() != null) {
			for (String groupId : request.getGroupIds()) {
				List<Terminal> byGroupId = terminalRepository
						.findByGroupId(groupId);
				Iterator<Terminal> iterator = byGroupId.iterator();
				while (iterator.hasNext()) {
					RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
					remindGroupTerminal.setRemindGroupId(remindGroup.getId());
					remindGroupTerminal.setTerminalId(iterator.next().getId());
					remindGroupTerminals.add(remindGroupTerminal);
				}
			}
		}
		
		String searchParams = request.getSearchParams();
		if (!StringUtils.isEmpty(searchParams)) {
			try {
				addSearchedTerminals(remindGroup, remindGroupTerminals,
						searchParams);
			} catch (Exception e) {
				logger.error("Error: ", e);
				return new ResponseEntity<>("Error while do searching.",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		remindGroupTerminalRepository.save(remindGroupTerminals);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private void addSearchedTerminals(RemindGroup remindGroup,
			List<RemindGroupTerminal> remindGroupTerminals, String searchParams) {
		String url = env.getRequiredProperty("terminalUser.search")
				+ searchParams;
		String response = restTemplate.getForObject(url, String.class);
		List<String> terminalIds = JsonPath.read(response,
				"$.content[*].terminal.id");
		for (String terminalId : terminalIds) {
			RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
			remindGroupTerminal.setRemindGroupId(remindGroup.getId());
			remindGroupTerminal.setTerminalId(terminalId);
			remindGroupTerminals.add(remindGroupTerminal);
		}
		Integer totalPages = JsonPath.read(response, "$.totalPages");
		if (totalPages > 1) {
			for (int i = 1; i < totalPages; i++) {
				String pagedURL = url + "&page=" + i;
				String pagedResponse = restTemplate.getForObject(pagedURL,
						String.class);
				List<String> pagedTerminalIds = JsonPath.read(pagedResponse,
						"$.content[*].terminal.id");
				for (String terminalId : pagedTerminalIds) {
					RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
					remindGroupTerminal.setRemindGroupId(remindGroup.getId());
					remindGroupTerminal.setTerminalId(terminalId);
					remindGroupTerminals.add(remindGroupTerminal);
				}
			}
		}
	}
	
	@RequestMapping(value = "/remindgroups/{id}/addterminals", method = RequestMethod.POST)
	public ResponseEntity<?> addTerminalsToRemindGroup(@PathVariable String id,
			@RequestBody AddTerminalsToRemindGroupRequest request) {
		
		RemindGroup remindGroup = remindGroupRepository.findOne(id);
		if (remindGroup == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<RemindGroupTerminal> remindGroupTerminals = new ArrayList<>();
		if (request.getTerminalIds() != null) {
			Iterable<Terminal> findAll = terminalRepository.findAll(request
					.getTerminalIds());
			Iterator<Terminal> iterator = findAll.iterator();
			while (iterator.hasNext()) {
				RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
				remindGroupTerminal.setRemindGroupId(remindGroup.getId());
				remindGroupTerminal.setTerminalId(iterator.next().getId());
				remindGroupTerminals.add(remindGroupTerminal);
			}
		}
		if (request.getGroupIds() != null) {
			for (String groupId : request.getGroupIds()) {
				List<Terminal> byGroupId = terminalRepository
						.findByGroupId(groupId);
				Iterator<Terminal> iterator = byGroupId.iterator();
				while (iterator.hasNext()) {
					RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
					remindGroupTerminal.setRemindGroupId(remindGroup.getId());
					remindGroupTerminal.setTerminalId(iterator.next().getId());
					remindGroupTerminals.add(remindGroupTerminal);
				}
			}
		}
		
		String searchParams = request.getSearchParams();
		if (!StringUtils.isEmpty(searchParams)) {
			try {
				addSearchedTerminals(remindGroup, remindGroupTerminals,
						searchParams);
			} catch (Exception e) {
				logger.error("Error: ", e);
				return new ResponseEntity<>("Error while do searching.",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		remindGroupTerminalRepository.save(remindGroupTerminals);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/remindgroups/{id}/deleteterminals", method = RequestMethod.POST)
	public ResponseEntity<?> deleteTerminalsFromRemindGroup(
			@PathVariable String id, @RequestBody List<String> terminalIds) {
		
		for (int i = 0; i < terminalIds.size(); i++) {
			String terminalId = terminalIds.get(i);
			RemindGroupTerminal remindGroupTerminal = remindGroupTerminalRepository
					.findByRemindGroupIdAndTerminalId(id, terminalId);
			if (remindGroupTerminal != null) {
				remindGroupTerminalRepository.delete(remindGroupTerminal);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/remindgroups/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteRemindGroup(@PathVariable String id) {
		RemindGroup remindGroup = remindGroupRepository.findById(id);
		List<RemindGroupTerminal> byRemindGroupId = remindGroupTerminalRepository
				.findByRemindGroupId(id);
		remindGroupTerminalRepository.delete(byRemindGroupId);
		if (remindGroup != null) {
			remindGroupRepository.delete(remindGroup);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private List<String> addList(Group group, List<String> groupIdList) {
		groupIdList.add(group.getId());
		List<Group> childrens = group.getChildrens();
		if (!CollectionUtils.isEmpty(childrens)) {
			for (Group child : childrens) {
				addList(child, groupIdList);
			}
		}
		return groupIdList;
	}
	
}
