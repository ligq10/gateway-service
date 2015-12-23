package com.changhongit.loving.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.support.RepositoryConstraintViolationExceptionMessage;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.changhongit.loving.UtilValue;
import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.HeartBeat;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.entity.TerminalUser;
import com.changhongit.loving.model.TerminalType;
import com.changhongit.loving.repository.GroupRepository;
import com.changhongit.loving.repository.HeartbeatRepository;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.repository.TerminalUserRepository;
import com.changhongit.loving.request.Cp150sDistributionRequest;
import com.changhongit.loving.request.TerminalBindGroupRequest;
import com.changhongit.loving.request.TerminalImportRequest;
import com.changhongit.loving.request.TerminalMoveRequest;
import com.changhongit.loving.request.TerminalRegisterRequest;
import com.changhongit.loving.request.UserSyncRequest;
import com.changhongit.loving.response.Location;
import com.changhongit.loving.response.TerminalResponse;
import com.changhongit.loving.validator.TerminalBindGroupValidator;
import com.changhongit.loving.validator.TerminalImportValidator;
import com.changhongit.loving.validator.TerminalMoveValidator;
import com.changhongit.loving.validator.TerminalRegisterValidator;
import com.google.common.collect.Lists;

@RestController
@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 200)
public class Cp150Controler {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String X_TOKEN = "X-Token";
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final String HAL_JSON = "application/hal+json";
	
	@Autowired
	private Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private TerminalRestRepository cp150Repository;
	
	@Autowired
	private TerminalUserRepository terminalUserRepository;
	
	@Autowired
	TerminalImportValidator terminalImportValidator;
	
	@Autowired
	TerminalRegisterValidator terminalRegisterValidator;
	
	@Autowired
	TerminalMoveValidator terminalMoveValidator;
	
	@Autowired
	TerminalBindGroupValidator bindGroupValidator;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private HeartbeatRepository heartbeatRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "/cp150s/import", method = { RequestMethod.POST })
	public HttpEntity<?> importCp150s(
			@RequestParam(value = "groupid", defaultValue = "", required = false) String groupId,
			@RequestParam("file") MultipartFile file,
			TerminalImportRequest terminalRequest, BindingResult errors) {
		
		if (!file.isEmpty()) {
			try {
				Workbook workbook = WorkbookFactory.create(file
						.getInputStream());
				Sheet sheet = workbook.getSheetAt(0);
				List<Terminal> terminals = new ArrayList<>();
				Map<String, Boolean> map = new HashMap<>();
				for (int r = sheet.getFirstRowNum() + 1; r <= sheet
						.getLastRowNum(); r++) {
					Row row = sheet.getRow(r);
					if (row == null) {
						continue;
					}
					terminalRequest.setGroupId(groupId);
					terminalRequest.setRowNum(r + 1);
					if (groupId.equals(UtilValue.GHT_ID)) {
						terminalRequest.setImei(getCellValue(row, 2));
						terminalRequest.setSn(getCellValue(row, 1));
						terminalRequest.setCheckCode(getCellValue(row, 3));
						terminalRequest.setModelNumber(getCellValue(row, 4));
						terminalRequest.setSim(getCellValue(row, 5));
					} else {
						terminalRequest.setImei(getCellValue(row, 1));
					}
					terminalImportValidator.validate(terminalRequest, errors);
					
					if (map.get(terminalRequest.getImei()) != null
							&& map.get(terminalRequest.getImei())) {
						errors.rejectValue("imei", "imei.invalid", String
								.format("第%s行%s字段:%s   %s", r + 1, "IMEI",
										terminalRequest.getImei(), "重复"));
					}
					if (errors.hasErrors()) {
						throw new RepositoryConstraintViolationException(errors);
					}
					map.put(terminalRequest.getImei(), true);
					
					if (groupId.equals(UtilValue.GHT_ID)) {
						Terminal terminal = new Terminal();
						BeanUtils.copyProperties(terminalRequest, terminal);
						terminals.add(terminal);
					} else {
						Terminal terminal = cp150Repository
								.findByImei(terminalRequest.getImei());
						terminal.setGroupId(groupId);
						terminals.add(terminal);
					}
				}
				Iterator<Terminal> iterator = cp150Repository.save(terminals)
						.iterator();
				if (iterator.hasNext()) {
					Terminal terminal = iterator.next();
					creatTerminalUser(terminal);
					userSync(env.getProperty(X_TOKEN), terminal);
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (InvalidFormatException e) {
				logger.debug(e.getMessage());
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (IOException e) {
				logger.debug(e.getMessage());
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
		}
	}
	
	private void userSync(String token, Terminal terminal) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.put(
				"Content-Type",
				Lists.newArrayList("application/vnd.jiahua.commands.syncaccount+json"));
		headers.put(X_TOKEN, Lists.newArrayList(token));
		UserSyncRequest userSyncRequest = new UserSyncRequest();
		userSyncRequest.setLoginName(terminal.getImei());
		userSyncRequest.setPassword(terminal.getImei());
		userSyncRequest.setUuid(terminal.getId());
		restTemplate.exchange(env.getRequiredProperty("users.syncaccount"),
				HttpMethod.POST, new HttpEntity<>(userSyncRequest, headers),
				ResponseEntity.class);
	}
	
	@RequestMapping(value = "/cp150s/register", method = { RequestMethod.POST })
	public HttpEntity<?> terminalRegister(
			@RequestBody TerminalRegisterRequest terminalRegisterRequest,
			BindingResult errors) {
		terminalRegisterValidator.validate(terminalRegisterRequest, errors);
		if (errors.hasErrors()) {
			throw new RepositoryConstraintViolationException(errors);
		}
		String imei = terminalRegisterRequest.getImei();
		Terminal terminal = cp150Repository.findByImei(imei);
		if (terminal == null) {
			terminal = new Terminal();
			terminal.setImei(imei);
			terminal.setCheckCode(imei.substring(16));
			terminal.setSim(terminalRegisterRequest.getSim());
			terminal.setType(TerminalType.App);
			cp150Repository.save(terminal);
			TerminalUser terminalUser = terminalUserRepository
					.findByTerminalImei(imei);
			if (terminalUser == null) {
				terminalUser = new TerminalUser();
				terminalUser.init();
				terminalUser.setTerminalImei(imei);
				terminalUser.setTerminalCheckCode(imei.substring(16));
				terminal.setSim(terminalRegisterRequest.getSim());
				terminalUser.setTerminal(terminal);
				terminalUserRepository.save(terminalUser);
			}
		}
		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cp150s/withterminaluser", method = { RequestMethod.GET }, produces = HAL_JSON)
	public Object getCp150sWithTerminalUser(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "20") int size,
			HttpServletRequest request) {
		Pageable pageable = new PageRequest(page, size);
		Page<Terminal> terminalPage = cp150Repository.findAll(pageable);
		if (CollectionUtils.isEmpty(terminalPage.getContent())) {
			return HttpStatus.NO_CONTENT;
		}
		String pathParams = "?";
		List<Link> list = prepareLinks(page, size, request, terminalPage,
				pathParams);
		List<TerminalResponse> content = prepareTerminalResponse(terminalPage);
		PagedResources<TerminalResponse> pagedResources = new PagedResources<>(
				content, new PageMetadata(terminalPage.getSize(),
						terminalPage.getNumber(),
						terminalPage.getTotalElements(),
						terminalPage.getTotalPages()), list);
		return new ResponseEntity(pagedResources, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cp150s/groupbind", method = { RequestMethod.POST })
	public Object cp150sGoupBind(
			@RequestParam(value = "parentid", defaultValue = "", required = false) String parentId,
			@RequestParam(value = "groupid", defaultValue = "", required = false) String groupId,
			@RequestParam("file") MultipartFile file,
			TerminalBindGroupRequest bindGroupRequest, BindingResult errors) {
		bindGroupRequest.setGroupid(groupId);
		bindGroupRequest.setParentid(parentId);
		if (!file.isEmpty()) {
			try {
				Workbook workbook = WorkbookFactory.create(file
						.getInputStream());
				Sheet sheet = workbook.getSheetAt(0);
				List<Terminal> terminals = new ArrayList<>();
				for (int r = sheet.getFirstRowNum() + 1; r <= sheet
						.getLastRowNum(); r++) {
					
					Row row = sheet.getRow(r);
					if (row == null) {
						continue;
					}
					bindGroupRequest.setRowNum(r + 1);
					bindGroupRequest.setImei(getCellValue(row, 1));
					bindGroupValidator.validate(bindGroupRequest, errors);
					if (errors.hasErrors()) {
						throw new RepositoryConstraintViolationException(errors);
					}
					String imei = row.getCell(1).getStringCellValue();
					Terminal terminal = cp150Repository.findByImei(imei);
					terminal.setGroupId(groupId);
					terminals.add(terminal);
				}
				cp150Repository.save(terminals);
				return HttpStatus.OK;
			} catch (InvalidFormatException e) {
				logger.debug(e.getMessage());
				return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
			} catch (IOException e) {
				logger.debug(e.getMessage());
				return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
			
		}
	}
	
	@RequestMapping(value = "/cp150s/movetoparent", method = { RequestMethod.POST })
	public Object moveToParent(
			@RequestBody TerminalMoveRequest terminalMoveRequest,
			BindingResult errors) {
		terminalMoveValidator.validate(terminalMoveRequest, errors);
		if (errors.hasErrors()) {
			throw new RepositoryConstraintViolationException(errors);
		}
		Terminal terminal = cp150Repository
				.findOne(terminalMoveRequest.getId());
		Group group = groupRepository.findOne(terminalMoveRequest.getGroupid());
		terminal.setGroupId(group.getParent().getId());
		cp150Repository.save(terminal);
		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cp150s/distribution", method = { RequestMethod.POST })
	public Object cp150sDistribution(
			@RequestBody Cp150sDistributionRequest distributionRequest,
			BindingResult result) {
		List<Terminal> terminals = new ArrayList<>();
		String toGroupId = distributionRequest.getToGroupId();
		if (!CollectionUtils.isEmpty(distributionRequest.getFromGroupIds())) {
			for (String fromGroupId : distributionRequest.getFromGroupIds()) {
				Group group = groupRepository.findOne(fromGroupId);
				terminals = addList(group, terminals, toGroupId);
			}
		}
		if (!CollectionUtils.isEmpty(distributionRequest.getFromTerminalIds())) {
			for (String terminalId : distributionRequest.getFromTerminalIds()) {
				Terminal terminal = cp150Repository.findOne(terminalId);
				if (terminal != null) {
					terminal.setGroupId(toGroupId);
					terminals.add(terminal);
				}
			}
		}
		cp150Repository.save(terminals);
		return HttpStatus.OK;
		
	}
	
	@RequestMapping(value = "/cp150s/{id}/locations", method = { RequestMethod.GET }, produces = HAL_JSON)
	public ResponseEntity<?> cp150Locations(
			@PathVariable String id,
			@RequestParam String from,
			@RequestParam String to,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "20") int size,
			HttpServletRequest request) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		try {
			Terminal terminal = cp150Repository.findOne(id);
			if (terminal == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			Pageable pageable = new PageRequest(page, size);
			Date fromDate = dateFormat.parse(from);
			Date toDate = dateFormat.parse(to);
			Page<HeartBeat> result = heartbeatRepository
					.findByImeiAndDateBetween(terminal.getImei(), fromDate,
							toDate, pageable);
			if (CollectionUtils.isEmpty(result.getContent())) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			String pathParams = "?from=" + from + "&to=" + to + "&";
			List<Link> list = prepareLinks(page, size, request, result,
					pathParams);
			List<Location> content = prepareLocations(result);
			PagedResources<Location> pagedResources = new PagedResources<Location>(
					content, new PageMetadata(result.getSize(),
							result.getNumber(), result.getTotalElements(),
							result.getTotalPages()), list);
			return new ResponseEntity(pagedResources, HttpStatus.OK);
		} catch (ParseException e) {
			logger.error("Error: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/cp150s/search/byKeywordAndGroup", produces = "application/hal+json")
	public HttpEntity<?> searchTerminals(
			@RequestParam String keyword,
			@RequestParam String groupid,
			@PageableDefault(page = 0, size = 20, sort = "imei", direction = Direction.DESC) Pageable pageRequest,
			HttpServletRequest request) {
		List<String> groupIdList = new ArrayList<>();
		Group group = groupRepository.findOne(groupid);
		if (group != null) {
			groupIdList = addList(group, groupIdList);
		}
		if (CollectionUtils.isEmpty(groupIdList)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		Page<Terminal> results = cp150Repository.findByKeyWordInGroup(keyword,
				groupIdList, pageRequest);
		List<Terminal> terminalList = results.getContent();
		if (results.getContent().isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			String pathParams = "?keyword=" + keyword + "&groupid=" + groupid
					+ "&";
			List<Link> links = prepareLinks(pageRequest.getPageNumber(),
					pageRequest.getPageSize(), request, results, pathParams);
			PageMetadata pageMetadata = new PageMetadata(results.getSize(),
					results.getNumber(), results.getTotalElements(),
					results.getTotalPages());
			PagedResources<Terminal> terminalPagedResources = new PagedResources<>(
					terminalList, pageMetadata, links);
			return new ResponseEntity<>(terminalPagedResources, HttpStatus.OK);
			
		}
	}
	
	@RequestMapping(value = "/cp150s/search/findByGroupIdAndChildGroups", method = { RequestMethod.GET }, produces = HAL_JSON)
	public Object findByGroupIdAndChildGroups(
			
			@RequestParam String groupid,
			@PageableDefault(page = 0, size = 20, sort = "imei", direction = Direction.DESC) Pageable pageRequest,
			HttpServletRequest request) {
		List<String> groupIdList = new ArrayList<>();
		Group group = groupRepository.findOne(groupid);
		if (group != null) {
			groupIdList = addList(group, groupIdList);
		}
		if (CollectionUtils.isEmpty(groupIdList)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		Page<Terminal> result = cp150Repository.findByGroupIdIn(groupIdList,
				pageRequest);
		String pathParams = "?groupid=" + groupid + "&";
		List<Link> list = prepareLinks(pageRequest.getPageNumber(),
				pageRequest.getPageSize(), request, result, pathParams);
		PagedResources<Terminal> pagedResources = new PagedResources<Terminal>(
				result.getContent(), new PageMetadata(result.getSize(),
						result.getNumber(), result.getTotalElements(),
						result.getTotalPages()), list);
		return new ResponseEntity(pagedResources, HttpStatus.OK);
		
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
	
	private List<TerminalResponse> prepareTerminalResponse(Page<Terminal> result) {
		List<TerminalResponse> cp150s = new ArrayList<>();
		DateFormat dateFormatResponse = new SimpleDateFormat(DATE_FORMAT);
		for (Terminal terminal : result.getContent()) {
			TerminalResponse terminalResponse = new TerminalResponse();
			BeanUtils.copyProperties(terminal, terminalResponse);
			TerminalUser terminalUser = terminalUserRepository
					.findByTerminalImeiAndTerminalCheckCode(terminal.getImei(),
							terminal.getCheckCode());
			if (terminalUser != null) {
				terminalResponse
						.setTerminalUserName(terminalUser.getRealName());
			}
			if (terminal.getActivateTime() != null) {
				terminalResponse.setActivateTime(dateFormatResponse
						.format(terminal.getActivateTime()));
			} else {
				terminalResponse.setActivateTime("");
			}
			cp150s.add(terminalResponse);
		}
		return cp150s;
	}
	
	private List<Location> prepareLocations(Page<HeartBeat> result) {
		List<Location> locations = new ArrayList<>();
		DateFormat dateFormatResponse = new SimpleDateFormat(DATE_FORMAT);
		for (HeartBeat heartBeat : result.getContent()) {
			Location location = new Location();
			location.setGpsStatus(heartBeat.getGpsStatus());
			if (heartBeat.getGpsStatus().equals(1)) {
				location.setLatitude(heartBeat.getLatitude());
				location.setLongitude(heartBeat.getLongitude());
			} else {
				location.setCell(heartBeat.getCell());
				location.setMcc(heartBeat.getMcc());
				location.setLac(heartBeat.getLac());
				location.setMnc(heartBeat.getMnc());
			}
			
			if (heartBeat.getDate() != null) {
				location.setDate(dateFormatResponse.format(heartBeat.getDate()));
			}
			locations.add(location);
		}
		return locations;
	}
	
	private List<Link> prepareLinks(int page, int size,
			HttpServletRequest request, Page result, String pathParams) {
		List<Link> list = new ArrayList<>();
		String host = getHost(request);
		if (result.hasNext()) {
			list.add(new Link(host + request.getRequestURI() + pathParams
					+ "page=" + (page + 1) + "&size=" + size, Link.REL_NEXT));
		}
		if (result.hasPrevious()) {
			list.add(new Link(host + request.getRequestURI() + pathParams
					+ "page=" + (page - 1) + "&size=" + size, Link.REL_PREVIOUS));
		}
		list.add(new Link(host + request.getRequestURI() + pathParams + "page="
				+ page + "&size=" + size, Link.REL_SELF));
		return list;
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
	
	public <T> ResponseEntity<T> response(HttpHeaders headers, T body,
			HttpStatus status) {
		HttpHeaders hdrs = new HttpHeaders();
		if (null != headers) {
			hdrs.putAll(headers);
		}
		return new ResponseEntity<>(body, hdrs, status);
	}
	
	private List<Terminal> addList(Group group, List<Terminal> terminals,
			String toGroupId) {
		for (Terminal fromTerminal : cp150Repository.findByGroupId(group
				.getId())) {
			fromTerminal.setGroupId(toGroupId);
			terminals.add(fromTerminal);
		}
		if (group.getChildrens() != null && !group.getChildrens().isEmpty()) {
			for (Group child : group.getChildrens()) {
				
				addList(child, terminals, toGroupId);
			}
		}
		return terminals;
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
	
	private String getCellValue(Row row, int column) {
		Cell cell = row.getCell(column);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			return cell.getStringCellValue();
		}
		return "";
	}
	
	private void creatTerminalUser(Terminal terminal) {
		TerminalUser terminalUser = new TerminalUser();
		terminalUser.init();
		terminalUser.setTerminalImei(terminal.getImei());
		terminalUser.setTerminalCheckCode(terminal.getCheckCode());
		terminalUser.setTerminal(terminal);
		terminalUserRepository.save(terminalUser);
	}
}