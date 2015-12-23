package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.changhongit.loving.model.ReminderMode;

/**
 * @author 73
 * 
 */
@Entity
public class Reminder {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private int terminalCount;
	
	private String ownerGroupId;
	
	private String creator;
	
	private boolean needIssue = true;
	
	private boolean needExport = true;
	
	private String searchKeywords;
	
	private String content;
	
	private String remindGroupId;
	
	@Transient
	private String remindGroupName;
	
	private Boolean active = true;
	
	private ReminderMode mode = ReminderMode.OneTime;
	
	private Date reminderTime;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getTerminalCount() {
		return terminalCount;
	}
	
	public void setTerminalCount(int terminalCount) {
		this.terminalCount = terminalCount;
	}
	
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	
	public boolean isNeedIssue() {
		return needIssue;
	}
	
	public void setNeedIssue(boolean needIssue) {
		this.needIssue = needIssue;
	}
	
	public boolean isNeedExport() {
		return needExport;
	}
	
	public void setNeedExport(boolean needExport) {
		this.needExport = needExport;
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
	
	public String getSearchKeywords() {
		return searchKeywords;
	}
	
	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getRemindGroupName() {
		return remindGroupName;
	}
	
	public void setRemindGroupName(String remindGroupName) {
		this.remindGroupName = remindGroupName;
	}
	
}
