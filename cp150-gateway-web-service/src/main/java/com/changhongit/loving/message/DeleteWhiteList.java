package com.changhongit.loving.message;

import java.io.Serializable;

public class DeleteWhiteList implements
		Cp150Message<Integer>, Serializable {

	private String imei;
	private Integer message;

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public void setImei(String imei) {
		this.imei = imei;
	}

    @Override
    public Integer getMessage() {
        return message;
    }

    @Override
    public void setMessage(Integer message) {
        this.message = message;
    }
}
