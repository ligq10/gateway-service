package com.changhongit.loving.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.changhongit.loving.document.Cp150Contacts;
import com.changhongit.loving.entity.Group;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.jpaRepository.GroupRepository;
import com.changhongit.loving.jpaRepository.TerminalRepository;
import com.changhongit.loving.repository.Cp150ContactsRepository;

@Controller
public class TerminalContorller {
	
	private static final String HAL_JSON = "application/hal+json";
	
	@Autowired
	private Cp150ContactsRepository cp150ContactsRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@RequestMapping(value = "cp150s/view/byContactAndPrentGroup", method = RequestMethod.GET, produces = HAL_JSON)
	public ResponseEntity<?> getOnlineTerminalsCountInGroups(
			@RequestParam(value = "telNum") String telNum,
			@RequestParam(value = "groupid") String parentGroupId,
			@PageableDefault(page = 0, size = 20, sort = "imei", direction = Direction.DESC) Pageable pageRequest,
			HttpServletRequest request) {
		List<Cp150Contacts> cp150Contacts = cp150ContactsRepository
				.findByContactsTelNum(telNum);
		List<String> imeis = new ArrayList<>();
		for (Cp150Contacts cp150Contact : cp150Contacts) {
			imeis.add(cp150Contact.getImei());
		}
		
		List<String> groupIdList = new ArrayList<>();
		Group group = groupRepository.findOne(parentGroupId);
		if (group != null) {
			groupIdList = addList(group, groupIdList);
		}
		if (CollectionUtils.isEmpty(groupIdList)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		Page<Terminal> terminals;
		if (CollectionUtils.isEmpty(imeis)) {
			terminals = terminalRepository.findBySimAndInGroup(telNum,
					groupIdList, pageRequest);
		} else {
			terminals = terminalRepository.findBySimAndInImeiAndInGroup(telNum,
					imeis, groupIdList, pageRequest);
		}
		
		String pathParams = "?telNum=" + telNum + "&groupid=" + parentGroupId
				+ "&";
		List<Link> links = prepareLinks(pageRequest.getPageNumber(),
				pageRequest.getPageSize(), request, terminals, pathParams);
		PageMetadata pageMetadata = new PageMetadata(terminals.getSize(),
				terminals.getNumber(), terminals.getTotalElements(),
				terminals.getTotalPages());
		List<Terminal> terminalList = terminals.getContent();
		PagedResources<Terminal> terminalPagedResources = new PagedResources<>(
				terminalList, pageMetadata, links);
		return new ResponseEntity<>(terminalPagedResources, HttpStatus.OK);
	};
	
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
}
