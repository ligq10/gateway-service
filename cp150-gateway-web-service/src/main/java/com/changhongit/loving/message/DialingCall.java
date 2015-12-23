package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.Call;

public class DialingCall implements Cp150Message<Call>, Serializable {

	private String imei;
	private Call message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public Call getMessage() {
		return message;
	}

	@Override
	public void setMessage(Call message) {
		this.message = message;
	}
}
