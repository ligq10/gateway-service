package com.changhongit.loving.resource;

import org.springframework.hateoas.ResourceSupport;

import com.changhongit.loving.document.Cp150VoiceReminder;

public class Cp150VoiceRemindersResource extends ResourceSupport {

	private Cp150VoiceReminder voiceReminders;

	public Cp150VoiceRemindersResource(Cp150VoiceReminder voiceReminders) {
		this.voiceReminders = voiceReminders;
	}

	public String getImei() {
		return voiceReminders.getImei();
	}

	public Object getVoiceReminders() {
		return voiceReminders.getReminders();
	}

}
