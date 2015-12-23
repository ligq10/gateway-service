package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.SimpleParam;

public class SettingArea implements Cp150Message<SimpleParam>, Serializable {

	private String imei;
	private SimpleParam message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public SimpleParam getMessage() {
		return message;
	}

	@Override
	public void setMessage(SimpleParam message) {
		this.message = message;
	}
}
