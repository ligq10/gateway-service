package com.changhongit.loving.request;

public class TerminalRegisterRequest {
	
	private String imei;
	
	private String sim = "";
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String getSim() {
		return sim;
	}
	
	public void setSim(String sim) {
		this.sim = sim;
	}
	
}
