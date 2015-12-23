package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.ProtectedCircle;

public class SettingProtectedCircle implements Cp150Message<ProtectedCircle>,
		Serializable {

	private String imei;
	private ProtectedCircle message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public ProtectedCircle getMessage() {
		return message;
	}

	@Override
	public void setMessage(ProtectedCircle message) {
		this.message = message;
	}
}
