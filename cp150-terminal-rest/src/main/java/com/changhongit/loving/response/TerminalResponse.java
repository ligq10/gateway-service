package com.changhongit.loving.response;

public class TerminalResponse {
	
	private String id;
	
	private String sn;
	
	private String modelNumber;
	
	private String terminalUserName;
	
	private String imei;
	
	private String sim;
	
	private String checkCode;
	
	private String version;
	
	private String status;
	
	private String activateTime;
	
	private String description;
	
	private String groupId = "guanhutong";
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSn() {
		return sn;
	}
	
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public String getModelNumber() {
		return modelNumber;
	}
	
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String getCheckCode() {
		return checkCode;
	}
	
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getActivateTime() {
		return activateTime;
	}
	
	public void setActivateTime(String activateTime) {
		this.activateTime = activateTime;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getSim() {
		return sim;
	}
	
	public void setSim(String sim) {
		this.sim = sim;
	}
	
	public String getTerminalUserName() {
		return terminalUserName;
	}
	
	public void setTerminalUserName(String terminalUserName) {
		this.terminalUserName = terminalUserName;
	}
	
}
