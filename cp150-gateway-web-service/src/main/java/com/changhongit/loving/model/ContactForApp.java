package com.changhongit.loving.model;


public class ContactForApp {
	
	private String keyNum;
	
	private String telNum = "";
	
	private String name = "";
	
	public String getKeyNum() {
		return keyNum;
	}
	
	public void setKeyNum(String keyNum) {
		this.keyNum = keyNum;
	}
	
	public ContactForApp() {
	}
	
	public ContactForApp(String telNum, String name) {
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
