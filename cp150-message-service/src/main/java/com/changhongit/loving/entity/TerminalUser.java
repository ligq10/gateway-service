package com.changhongit.loving.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class TerminalUser {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	@Column(unique = true)
	private String terminalImei;
	
	private String terminalCheckCode;
	
	private String telNum;
	
	private String realName;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTerminalImei() {
		return terminalImei;
	}
	
	public void setTerminalImei(String terminalImei) {
		this.terminalImei = terminalImei;
	}
	
	public String getTerminalCheckCode() {
		return terminalCheckCode;
	}
	
	public void setTerminalCheckCode(String terminalCheckCode) {
		this.terminalCheckCode = terminalCheckCode;
	}
	
	public String getTelNum() {
		return telNum;
	}
	
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
}
