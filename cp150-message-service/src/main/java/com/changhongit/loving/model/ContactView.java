package com.changhongit.loving.model;

public class ContactView {
	
	private String index;
	
	private String telNum = "";
	
	private String name = "";
	
	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		this.index = index;
	}
	
	public ContactView() {
	}
	
	public ContactView(String telNum, String name) {
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
