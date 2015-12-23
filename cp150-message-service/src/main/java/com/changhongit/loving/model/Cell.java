package com.changhongit.loving.model;

public class Cell {
	
	private String radioType;
	
	private String mobileCountryCode;
	
	private String mobileNetworkCode;
	
	private String locationAreaCode;
	
	private String cellId;
	
	public String getRadioType() {
		return radioType;
	}
	
	public void setRadioType(String radioType) {
		this.radioType = radioType;
	}
	
	public String getMobileCountryCode() {
		return mobileCountryCode;
	}
	
	public void setMobileCountryCode(String mobileCountryCode) {
		this.mobileCountryCode = mobileCountryCode;
	}
	
	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}
	
	public void setMobileNetworkCode(String mobileNetworkCode) {
		this.mobileNetworkCode = mobileNetworkCode;
	}
	
	public String getLocationAreaCode() {
		return locationAreaCode;
	}
	
	public void setLocationAreaCode(String locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}
	
	public String getCellId() {
		return cellId;
	}
	
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	
}
