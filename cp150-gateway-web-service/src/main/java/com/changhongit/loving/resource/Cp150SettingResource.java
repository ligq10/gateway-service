package com.changhongit.loving.resource;

import org.springframework.hateoas.ResourceSupport;

import com.changhongit.loving.document.Cp150Setting;

public class Cp150SettingResource extends ResourceSupport {

	private Cp150Setting cp150Setting;

	public Cp150SettingResource(Cp150Setting cp150Setting) {
		this.cp150Setting = cp150Setting;
	}

	public String getImei() {
		return cp150Setting.getImei();
	}

	public Object getSetting() {
		return cp150Setting.getSetting();
	}

}
