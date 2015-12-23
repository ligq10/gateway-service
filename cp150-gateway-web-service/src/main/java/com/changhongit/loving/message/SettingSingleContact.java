package com.changhongit.loving.message;

import com.changhongit.loving.model.SettingContact;

import java.io.Serializable;

public class SettingSingleContact implements
		Cp150Message<SettingContact>, Serializable {

	private String imei;
	private SettingContact message;

    @Override
    public String getImei() {
        return imei;
    }

    @Override
    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public SettingContact getMessage() {
        return message;
    }

    @Override
    public void setMessage(SettingContact message) {
        this.message = message;
    }
}
