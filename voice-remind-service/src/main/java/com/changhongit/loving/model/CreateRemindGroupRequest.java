package com.changhongit.loving.model;

import java.util.List;

public class CreateRemindGroupRequest {
	
	private String name;
	
	private String ownerGroupId;
	
	private List<String> searchKeywords;
	
	private String searchParams;
	
	private List<String> groupIds;
	
	private List<String> terminalIds;
	
	public List<String> getSearchKeywords() {
		return searchKeywords;
	}
	
	public void setSearchKeywords(List<String> searchKeywords) {
		this.searchKeywords = searchKeywords;
	}
	
	public String getSearchParams() {
		return searchParams;
	}
	
	public void setSearchParams(String searchParams) {
		this.searchParams = searchParams;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	
	public List<String> getGroupIds() {
		return groupIds;
	}
	
	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}
	
	public List<String> getTerminalIds() {
		return terminalIds;
	}
	
	public void setTerminalIds(List<String> terminalId) {
		this.terminalIds = terminalId;
	}
}
