package com.changhongit.loving.model;

import java.io.Serializable;

public class DeleteVoiceReminder implements Cp150Message<Integer>,
		Serializable {

	private String imei;

    @Override
    public Integer getMessage() {
        return message;
    }

    @Override
    public void setMessage(Integer message) {
        this.message = message;
    }

    private Integer message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}



}
