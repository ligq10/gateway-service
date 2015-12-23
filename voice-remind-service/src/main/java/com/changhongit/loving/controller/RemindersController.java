package com.changhongit.loving.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.changhongit.loving.entity.Reminder;
import com.changhongit.loving.entity.ReminderExportFile;
import com.changhongit.loving.entity.ReminderTerminal;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.model.CreateReminderRequest;
import com.changhongit.loving.model.PatchReminderRequest;
import com.changhongit.loving.model.ReminderExportsResponse;
import com.changhongit.loving.model.ReminderIdResponse;
import com.changhongit.loving.model.ReminderResponse;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.RemindGroupRepository;
import com.changhongit.loving.repository.RemindGroupTerminalRepository;
import com.changhongit.loving.repository.ReminderExportFileRepository;
import com.changhongit.loving.repository.ReminderRepository;
import com.changhongit.loving.repository.ReminderTerminalRepository;
import com.changhongit.loving.repository.TerminalRepository;
import com.jayway.jsonpath.JsonPath;

@Controller
public class RemindersController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private ReminderRepository reminderRepository;
	
	@Autowired
	private ReminderTerminalRepository reminderTerminalRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private RemindGroupRepository remindGroupRepository;
	
	@Autowired
	private ReminderExportFileRepository reminderExportFileRepository;
	
	@Autowired
	private RemindGroupTerminalRepository remindGroupTerminalRepository;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "/reminders/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getReminder(@PathVariable String id,
			@PageableDefault(page = 0, size = 50) Pageable pageRequest) {
		
		Reminder reminder = reminderRepository.findOne(id);
		if (reminder == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			if (reminder.getRemindGroupId() != null) {
				reminder.setRemindGroupName(remindGroupRepository.findById(
						reminder.getRemindGroupId()).getName());
			}
			final Page<ReminderTerminal> byReminderId = reminderTerminalRepository
					.findByReminderId(id, pageRequest);
			List<String> terminalIds = new ArrayList<>();
			for (ReminderTerminal reminderTerminal : byReminderId) {
				terminalIds.add(reminderTerminal.getTerminalId());
			}
			
			Iterable<Terminal> terminals = terminalRepository
					.findAll(terminalIds);
			final List<Terminal> content = new ArrayList<>();
			Iterator<Terminal> iterator = terminals.iterator();
			while (iterator.hasNext()) {
				content.add(iterator.next());
			}
			ReminderResponse response = new ReminderResponse(byReminderId,
					reminder, content);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/reminders/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteReminder(@PathVariable String id) {
		List<ReminderTerminal> reminderTerminals = reminderTerminalRepository
				.findByReminderId(id);
		for (ReminderTerminal reminderTerminal : reminderTerminals) {
			reminderTerminal.setStatus(false);
		}
		reminderTerminalRepository.save(reminderTerminals);
		reminderRepository.delete(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reminders", method = RequestMethod.GET)
	public ResponseEntity<?> getReminders(
			@RequestParam(value = "groupId", required = false, defaultValue = "guanhutong") String parentOwnerGroupId,
			@RequestParam(value = "status", required = false) String status,
			@PageableDefault(page = 0, size = 20) Pageable pageRequest) {
		List<String> groupIdList = new ArrayList<>();
		Group group = groupRepository.findOne(parentOwnerGroupId);
		if (group != null) {
			groupIdList = addList(group, groupIdList);
		}
		if (CollectionUtils.isEmpty(groupIdList)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		Page<Reminder> reminderPage;
		if (status != null) {
			Boolean statusBoolean = Boolean.valueOf(status);
			if (statusBoolean) {
				reminderPage = reminderRepository
						.findByNeedIssueAndReminderTimeLessThanAndOwnerGroupIdInOrderByReminderTimeDesc(
								false, new Date(), groupIdList, pageRequest);
			} else {
				reminderPage = reminderRepository
						.findByNeedIssueOrReminderTimeGreaterThanAndOwnerGroupIdInOrderByReminderTimeDesc(
								true, new Date(), groupIdList, pageRequest);
			}
		} else {
			reminderPage = reminderRepository.findByOwnerGroupIdIn(groupIdList,
					pageRequest);
		}
		for (Reminder reminder : reminderPage.getContent()) {
			if (reminder.getRemindGroupId() != null) {
				RemindGroup remindGroup = remindGroupRepository
						.findById(reminder.getRemindGroupId());
				if (remindGroup != null) {
					
					reminder.setRemindGroupName(remindGroup.getName());
				}
			}
			reminder.setTerminalCount(reminderTerminalRepository
					.countByReminderId(reminder.getId()));
		}
		return new ResponseEntity<>(reminderPage, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reminders", method = RequestMethod.POST)
	public ResponseEntity<?> createReminder(
			@RequestBody CreateReminderRequest request) {
		RemindGroup remindGroup = creatRemindGroup(request);
		
		Reminder reminder = new Reminder();
		
		BeanUtils.copyProperties(request, reminder);
		
		if (remindGroup != null) {
			reminder.setRemindGroupId(remindGroup.getId());
		}
		
		if (request.getSearchKeywords() != null) {
			String keywords = "";
			for (String keyword : request.getSearchKeywords()) {
				keywords = keywords + keyword + " ";
			}
			reminder.setSearchKeywords(keywords);
		}
		
		reminderRepository.save(reminder);
		
		List<ReminderTerminal> reminderTerminals = new ArrayList<>();
		if (!StringUtils.isEmpty(reminder.getRemindGroupId())) {
			Iterator<RemindGroupTerminal> iterator = remindGroupTerminalRepository
					.findByRemindGroupId(reminder.getRemindGroupId())
					.iterator();
			while (iterator.hasNext()) {
				ReminderTerminal reminderTerminal = new ReminderTerminal();
				reminderTerminal.setReminderId(reminder.getId());
				reminderTerminal.setTerminalId(iterator.next().getTerminalId());
				reminderTerminals.add(reminderTerminal);
			}
		} else {
			if (request.getTerminalIds() != null) {
				Iterable<Terminal> findAll = terminalRepository.findAll(request
						.getTerminalIds());
				Iterator<Terminal> iterator = findAll.iterator();
				while (iterator.hasNext()) {
					ReminderTerminal reminderTerminal = new ReminderTerminal();
					reminderTerminal.setReminderId(reminder.getId());
					reminderTerminal.setTerminalId(iterator.next().getId());
					reminderTerminals.add(reminderTerminal);
				}
			}
			if (request.getGroupIds() != null) {
				for (String groupId : request.getGroupIds()) {
					List<Terminal> byGroupId = terminalRepository
							.findByGroupId(groupId);
					Iterator<Terminal> iterator = byGroupId.iterator();
					while (iterator.hasNext()) {
						ReminderTerminal reminderTerminal = new ReminderTerminal();
						reminderTerminal.setReminderId(reminder.getId());
						reminderTerminal.setTerminalId(iterator.next().getId());
						reminderTerminals.add(reminderTerminal);
					}
				}
			}
			
			String searchParams = request.getSearchParams();
			if (!StringUtils.isEmpty(searchParams)) {
				try {
					addSearchedTerminals(reminder, reminderTerminals,
							searchParams);
				} catch (Exception e) {
					logger.error("Error: ", e);
					return new ResponseEntity<>("Error while do searching.",
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		
		reminderTerminalRepository.save(reminderTerminals);
		
		ReminderIdResponse idResponse = new ReminderIdResponse(reminder.getId());
		
		return new ResponseEntity<>(idResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reminders/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchReminder(@PathVariable String id,
			@RequestBody PatchReminderRequest request) {
		
		Reminder reminder = reminderRepository.findOne(id);
		if (reminder == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.isEmpty(request.getContent())) {
			reminder.setContent(request.getContent());
		}
		if (request.getReminderTime() != null
				&& request.getReminderTime().after(new Date())) {
			reminder.setReminderTime(request.getReminderTime());
		}
		reminder.setNeedIssue(true);
		reminder.setNeedExport(true);
		reminderRepository.save(reminder);
		if (request.getSaveToRemindGroup()) {
			RemindGroup remindGroup = new RemindGroup();
			if (!StringUtils.isEmpty(reminder.getSearchKeywords())) {
				remindGroup.setSearchKeywords(reminder.getSearchKeywords());
			}
			remindGroup.setName(request.getReminderGroupName());
			remindGroup.setOwnerGroupId(reminder.getOwnerGroupId());
			remindGroupRepository.save(remindGroup);
			reminder.setRemindGroupId(remindGroup.getId());
			reminderRepository.save(reminder);
			String remindGroupId = remindGroup.getId();
			List<RemindGroupTerminal> remindGroupTerminals = new ArrayList<>();
			List<ReminderTerminal> findByReminderId = reminderTerminalRepository
					.findByReminderId(reminder.getId());
			for (ReminderTerminal reminderTerminal : findByReminderId) {
				RemindGroupTerminal remindGroupTerminal = new RemindGroupTerminal();
				remindGroupTerminal.setTerminalId(reminderTerminal
						.getTerminalId());
				remindGroupTerminal.setRemindGroupId(remindGroupId);
				remindGroupTerminals.add(remindGroupTerminal);
			}
			remindGroupTerminalRepository.save(remindGroupTerminals);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/reminders/{id}/exports", method = RequestMethod.GET)
	public ResponseEntity<?> getExports(@PathVariable String id,
			@PageableDefault(page = 0, size = 50) Pageable pageRequest) {
		List<ReminderExportFile> findByReminderId = reminderExportFileRepository
				.findByReminderId(id);
		int count = reminderTerminalRepository.countByReminderId(id);
		List<String> reminderExports = new ArrayList<>();
		for (ReminderExportFile reminderExportFile : findByReminderId) {
			reminderExports.add(reminderExportFile.getId());
		}
		ReminderExportsResponse reminderExportsResponse = new ReminderExportsResponse();
		reminderExportsResponse.setCount(count);
		reminderExportsResponse.setListSize(reminderExports.size());
		reminderExportsResponse.setReminderExports(reminderExports);
		return new ResponseEntity<>(reminderExportsResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/exports/{id}", method = RequestMethod.GET)
	public void getExportsFile(@PathVariable String id,
			final HttpServletResponse response) {
		ReminderExportFile reminderExportFile = reminderExportFileRepository
				.findOne(id);
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment;fileName=" + id
				+ ".xls");
		OutputStream outputStream;
		try {
			outputStream = new BufferedOutputStream(response.getOutputStream());
			outputStream.write(reminderExportFile.getExportFile());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	private RemindGroup creatRemindGroup(CreateReminderRequest request) {
		RemindGroup remindGroup = null;
		if (!StringUtils.isEmpty(request.getReminderGroupName())) {
			remindGroup = new RemindGroup();
			remindGroup.setName(request.getReminderGroupName());
			remindGroup.setOwnerGroupId(request.getOwnerGroupId());
			if (request.getSearchKeywords() != null) {
				String keywords = "";
				for (String keyword : request.getSearchKeywords()) {
					keywords = keywords + keyword + " ";
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
						remindGroupTerminal.setRemindGroupId(remindGroup
								.getId());
						remindGroupTerminal.setTerminalId(iterator.next()
								.getId());
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
				}
			}
			
			remindGroupTerminalRepository.save(remindGroupTerminals);
		}
		return remindGroup;
	}
	
	private void addSearchedTerminals(Reminder reminder,
			List<ReminderTerminal> reminderTerminals, String searchParams) {
		String url = env.getRequiredProperty("terminalUser.search")
				+ searchParams;
		String response = restTemplate.getForObject(url, String.class);
		List<String> terminalIds = JsonPath.read(response,
				"$.content[*].terminal.id");
		for (String terminalId : terminalIds) {
			ReminderTerminal reminderTerminal = new ReminderTerminal();
			reminderTerminal.setReminderId(reminder.getId());
			reminderTerminal.setTerminalId(terminalId);
			reminderTerminals.add(reminderTerminal);
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
					ReminderTerminal reminderTerminal = new ReminderTerminal();
					reminderTerminal.setReminderId(reminder.getId());
					reminderTerminal.setTerminalId(terminalId);
					reminderTerminals.add(reminderTerminal);
				}
			}
		}
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
	
}
