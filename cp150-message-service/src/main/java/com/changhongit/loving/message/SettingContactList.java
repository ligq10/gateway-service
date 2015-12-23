package com.changhongit.loving.message;

import java.io.Serializable;
import java.util.ArrayList;

import com.changhongit.loving.model.SettingContact;

public class SettingContactList implements
		Cp150Message<ArrayList<SettingContact>>, Serializable {
	private String imei;
	private ArrayList<SettingContact> message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public ArrayList<SettingContact> getMessage() {
		return message;
	}

	@Override
	public void setMessage(ArrayList<SettingContact> message) {
		this.message = message;
	}
}
