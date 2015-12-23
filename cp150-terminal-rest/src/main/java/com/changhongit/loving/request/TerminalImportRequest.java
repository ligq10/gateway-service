package com.changhongit.loving.request;

import java.util.Date;

public class TerminalImportRequest {
	
	private int rowNum;
	
	private String sn;
	
	private String modelNumber;
	
	private String imei;
	
	private String sim;
	
	private String checkCode;
	
	private Date activateTime;
	
	private String groupId;
	
	public int getRowNum() {
		return rowNum;
	}
	
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
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
	
	public String getSim() {
		return sim;
	}
	
	public void setSim(String sim) {
		this.sim = sim;
	}
	
	public String getCheckCode() {
		return checkCode;
	}
	
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	
	public Date getActivateTime() {
		return activateTime;
	}
	
	public void setActivateTime(Date activateTime) {
		this.activateTime = activateTime;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
