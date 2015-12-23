package com.changhongit.loving.resource;

import org.springframework.hateoas.ResourceSupport;

public class Cp150SettingsResource extends ResourceSupport {

	private String imei;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
