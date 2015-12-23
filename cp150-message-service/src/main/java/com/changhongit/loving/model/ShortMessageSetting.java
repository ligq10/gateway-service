package com.changhongit.loving.model;

import java.util.Date;

import org.msgpack.annotation.Message;

@Message
public class ShortMessageSetting {
	
	private String id;
	
	private String imei;
	
	private Date date;
	
	private String sentFrom;
	
	private String msgContent;
	
	public String getId() {
		return id;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getSentFrom() {
		return sentFrom;
	}
	
	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}
	
	public String getMsgContent() {
		return msgContent;
	}
	
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
}
