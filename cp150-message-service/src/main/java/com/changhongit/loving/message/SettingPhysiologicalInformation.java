package com.changhongit.loving.message;

import java.io.Serializable;

import com.changhongit.loving.model.PhysiologicalInformation;

public class SettingPhysiologicalInformation implements
		Cp150Message<PhysiologicalInformation>, Serializable {

	private String imei;
	private PhysiologicalInformation message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public PhysiologicalInformation getMessage() {
		return message;
	}

	@Override
	public void setMessage(PhysiologicalInformation message) {
		this.message = message;
	}
}
