package com.changhongit.loving.document;

import java.util.Date;

import org.msgpack.annotation.Message;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Message
public class Cp150Setting<T> {
	
	@Id
	private String id;
	
	private String imei;
	
	private Date lastUpdated;
	
	private String key;
	
	private T setting;
	
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
	
	public T getSetting() {
		return setting;
	}
	
	public void setSetting(T setting) {
		this.setting = setting;
	}
}
