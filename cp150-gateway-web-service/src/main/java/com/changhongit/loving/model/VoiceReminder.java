package com.changhongit.loving.model;

import java.io.Serializable;

public class VoiceReminder implements Serializable {

	private String reminderTime;
	private String mode;
	private String modeMean;
	private String action;
	private int index;
	private String content;
	private String status;

	public String getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(String reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModeMean() {
		return modeMean;
	}

	public void setModeMean(String modeMean) {
		this.modeMean = modeMean;
	}

}
