package com.changhongit.loving.request;

public class TerminalBindGroupRequest {
	
	private int rowNum;
	
	private String imei;
	
	private String groupid;
	
	private String parentid;
	
	public int getRowNum() {
		return rowNum;
	}
	
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String getGroupid() {
		return groupid;
	}
	
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	
	public String getParentid() {
		return parentid;
	}
	
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
}
