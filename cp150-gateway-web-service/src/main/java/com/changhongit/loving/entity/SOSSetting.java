package com.changhongit.loving.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.msgpack.annotation.Message;

@Entity
@Table(name = "SOSSETTING_TBL")
@Message
public class SOSSetting {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String sendShortMessage = "1";
	
	private String sentToC1 = "1";
	
	private String sentToC2 = "1";
	
	private String sentToC3 = "1";
	
	private String sentToC4 = "1";
	
	private String sentC5Tell = "";
	
	private String calling = "1";
	
	private String callC1 = "1";
	
	private String callC2 = "1";
	
	private String callC3 = "1";
	
	private String callC4 = "1";
	
	private String callC5Tell = "";
	
	private String smPrefix = "";
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSendShortMessage() {
		return sendShortMessage;
	}
	
	public void setSendShortMessage(String sendShortMessage) {
		this.sendShortMessage = sendShortMessage;
	}
	
	public String getSentToC1() {
		return sentToC1;
	}
	
	public void setSentToC1(String sentToC1) {
		this.sentToC1 = sentToC1;
	}
	
	public String getSentToC2() {
		return sentToC2;
	}
	
	public void setSentToC2(String sentToC2) {
		this.sentToC2 = sentToC2;
	}
	
	public String getSentToC3() {
		return sentToC3;
	}
	
	public void setSentToC3(String sentToC3) {
		this.sentToC3 = sentToC3;
	}
	
	public String getSentToC4() {
		return sentToC4;
	}
	
	public void setSentToC4(String sentToC4) {
		this.sentToC4 = sentToC4;
	}
	
	public String getSentC5Tell() {
		return sentC5Tell;
	}
	
	public void setSentC5Tell(String sentC5Tell) {
		this.sentC5Tell = sentC5Tell;
	}
	
	public String getCalling() {
		return calling;
	}
	
	public void setCalling(String calling) {
		this.calling = calling;
	}
	
	public String getCallC1() {
		return callC1;
	}
	
	public void setCallC1(String callC1) {
		this.callC1 = callC1;
	}
	
	public String getCallC2() {
		return callC2;
	}
	
	public void setCallC2(String callC2) {
		this.callC2 = callC2;
	}
	
	public String getCallC3() {
		return callC3;
	}
	
	public void setCallC3(String callC3) {
		this.callC3 = callC3;
	}
	
	public String getCallC4() {
		return callC4;
	}
	
	public void setCallC4(String callC4) {
		this.callC4 = callC4;
	}
	
	public String getCallC5Tell() {
		return callC5Tell;
	}
	
	public void setCallC5Tell(String callC5Tell) {
		this.callC5Tell = callC5Tell;
	}
	
	public String getSmPrefix() {
		return smPrefix;
	}
	
	public void setSmPrefix(String smPrefix) {
		this.smPrefix = smPrefix;
	}
	
}
