package com.changhongit.loving.resource;

import java.util.Date;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.changhongit.loving.model.VoiceReminderV2;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Cp150VoiceRemindersAppResource extends ResourceSupport {
	
	private String imei;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastUpdated;
	
	private List<VoiceReminderV2> voiceReminders;
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public List<VoiceReminderV2> getVoiceReminders() {
		return voiceReminders;
	}
	
	public void setVoiceReminders(List<VoiceReminderV2> voiceReminders) {
		this.voiceReminders = voiceReminders;
	}
	
}
