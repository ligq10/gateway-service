package com.changhongit.loving.message;

import java.io.Serializable;

public class Cp150DownMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String imei;
	
	private String message;
	
	public Cp150DownMessage() {
	}
	
	public Cp150DownMessage(String imei, String message) {
		this.imei = imei;
		this.message = message;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
