package com.changhongit.loving.document;

import com.changhongit.loving.model.VoiceReminderV2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document
public class Cp150VoiceReminder {

	@Id
	private String id;
	private String imei;
	private Date lastUpdated;
	private Map<String, VoiceReminderV2> reminders;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Map<String, VoiceReminderV2> getReminders() {
		return reminders;
	}

	public void setReminders(Map<String, VoiceReminderV2> reminders) {
		this.reminders = reminders;
	}

}
