package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.msgpack.annotation.Message;

@Entity
@Message
public class ShortMessage {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
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
