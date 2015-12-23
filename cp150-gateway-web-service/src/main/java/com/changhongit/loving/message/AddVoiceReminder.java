package com.changhongit.loving.message;

import com.changhongit.loving.model.VoiceReminderV2;

import java.io.Serializable;

public class AddVoiceReminder implements Cp150Message<VoiceReminderV2>,
		Serializable {

	private String imei;
	private VoiceReminderV2 message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public VoiceReminderV2 getMessage() {
		return message;
	}

	@Override
	public void setMessage(VoiceReminderV2 message) {
		this.message = message;
	}
}
