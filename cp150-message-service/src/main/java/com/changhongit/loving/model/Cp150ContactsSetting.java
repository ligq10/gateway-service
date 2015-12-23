package com.changhongit.loving.model;

import java.util.Date;
import java.util.HashMap;

import org.msgpack.annotation.Message;

@Message
public class Cp150ContactsSetting {
	
	private String id;
	
	private String imei;
	
	private Date lastUpdated;
	
	private String key;
	
	private HashMap<String, Contact> setting;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public HashMap<String, Contact> getSetting() {
		return setting;
	}
	
	public void setSetting(HashMap<String, Contact> setting) {
		this.setting = setting;
	}
	
}
