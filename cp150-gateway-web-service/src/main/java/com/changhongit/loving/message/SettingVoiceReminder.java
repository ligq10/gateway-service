package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.VoiceReminder;

public class SettingVoiceReminder implements Cp150Message<VoiceReminder>,
		Serializable {

	private String imei;
	private VoiceReminder message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public VoiceReminder getMessage() {
		return message;
	}

	@Override
	public void setMessage(VoiceReminder message) {
		this.message = message;
	}
}
