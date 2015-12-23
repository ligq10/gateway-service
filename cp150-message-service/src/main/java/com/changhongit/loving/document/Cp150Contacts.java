package com.changhongit.loving.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.changhongit.loving.model.ContactView;

@Document
public class Cp150Contacts {
	
	@Id
	private String id;
	
	private String imei;
	
	private String phone;
	
	private List<ContactView> contacts;
	
	private List<ContactView> whiteList;
	
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
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public List<ContactView> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<ContactView> contacts) {
		this.contacts = contacts;
	}
	
	public List<ContactView> getWhiteList() {
		return whiteList;
	}
	
	public void setWhiteList(List<ContactView> whiteList) {
		this.whiteList = whiteList;
	}
	
}
