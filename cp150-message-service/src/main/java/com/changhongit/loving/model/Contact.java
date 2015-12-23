package com.changhongit.loving.model;

import org.msgpack.annotation.Message;

@Message
public class Contact {
	
	private String telNum = "";
	
	private String name = "";
	
	public Contact() {
	}
	
	public Contact(String telNum, String name) {
		this.telNum = telNum;
		this.name = name;
	}
	
	public String getTelNum() {
		return telNum;
	}
	
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
