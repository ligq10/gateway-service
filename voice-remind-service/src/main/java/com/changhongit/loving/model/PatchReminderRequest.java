package com.changhongit.loving.model;

import java.util.Date;

public class PatchReminderRequest {
	
	private Boolean saveToRemindGroup = false;
	
	private Date reminderTime;
	
	private String content;
	
	private String reminderGroupName;
	
	public Boolean getSaveToRemindGroup() {
		return saveToRemindGroup;
	}
	
	public void setSaveToRemindGroup(Boolean saveToRemindGroup) {
		this.saveToRemindGroup = saveToRemindGroup;
	}
	
	public Date getReminderTime() {
		return reminderTime;
	}
	
	public void setReminderTime(Date reminderTime) {
		this.reminderTime = reminderTime;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getReminderGroupName() {
		return reminderGroupName;
	}
	
	public void setReminderGroupName(String reminderGroupName) {
		this.reminderGroupName = reminderGroupName;
	}
	
}
