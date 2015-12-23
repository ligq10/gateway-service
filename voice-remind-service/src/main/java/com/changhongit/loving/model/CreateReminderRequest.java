package com.changhongit.loving.model;

import java.util.Date;
import java.util.List;

public class CreateReminderRequest {
	
	private String reminderGroupName;
	
	private String creator;
	
	private String ownerGroupId;
	
	private String searchParams;
	
	private String remindGroupId;
	
	private List<String> searchKeywords;
	
	private List<String> groupIds;
	
	private List<String> terminalIds;
	
	private Boolean active = true;
	
	private ReminderMode mode = ReminderMode.OneTime;
	
	private Date reminderTime;
	
	private String content;
	
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	
	public String getSearchParams() {
		return searchParams;
	}
	
	public void setSearchParams(String searchParams) {
		this.searchParams = searchParams;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getRemindGroupId() {
		return remindGroupId;
	}
	
	public void setRemindGroupId(String remindGroupId) {
		this.remindGroupId = remindGroupId;
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
	
	public void setTerminalIds(List<String> terminalIds) {
		this.terminalIds = terminalIds;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public ReminderMode getMode() {
		return mode;
	}
	
	public void setMode(ReminderMode mode) {
		this.mode = mode;
	}
	
	public Date getReminderTime() {
		return reminderTime;
	}
	
	public void setReminderTime(Date reminderTime) {
		this.reminderTime = reminderTime;
	}
	
	public List<String> getSearchKeywords() {
		return searchKeywords;
	}
	
	public void setSearchKeywords(List<String> searchKeywords) {
		this.searchKeywords = searchKeywords;
	}
	
	public String getReminderGroupName() {
		return reminderGroupName;
	}
	
	public void setReminderGroupName(String reminderGroupName) {
		this.reminderGroupName = reminderGroupName;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
}
