package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.SOSConf;

public class SettingSos implements Cp150Message<SOSConf>, Serializable {

	private String imei;
	private SOSConf message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public SOSConf getMessage() {
		return message;
	}

	@Override
	public void setMessage(SOSConf message) {
		this.message = message;
	}
}
