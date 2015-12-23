package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.ShortMessage;

public class SendMultimediaMessage implements Cp150Message<ShortMessage>,
		Serializable {

	private String imei;
	private ShortMessage message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public ShortMessage getMessage() {
		return message;
	}

	@Override
	public void setMessage(ShortMessage message) {
		this.message = message;
	}
}
