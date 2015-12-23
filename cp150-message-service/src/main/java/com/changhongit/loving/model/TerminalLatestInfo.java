package com.changhongit.loving.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public class TerminalLatestInfo {
	
	private String sim;
	
	private Integer gpsStatus;
	
	private Float longitude;
	
	private Float latitude;
	
	private String mcc;
	
	private String mnc;
	
	private String lac;
	
	private String cell;
	
	private String locationTime;
	
	private String expireDate;
	
	private String batteryLevel;
	
	private Boolean onLine;
	
	private String chargeStatus = "未充电";
	
	public String getSim() {
		return sim;
	}
	
	public void setSim(String sim) {
		this.sim = sim;
	}
	
	public Integer getGpsStatus() {
		return gpsStatus;
	}
	
	public void setGpsStatus(Integer gpsStatus) {
		this.gpsStatus = gpsStatus;
	}
	
	public Float getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	public Float getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public String getMcc() {
		return mcc;
	}
	
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	
	public String getMnc() {
		return mnc;
	}
	
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	
	public String getLac() {
		return lac;
	}
	
	public void setLac(String lac) {
		this.lac = lac;
	}
	
	public String getCell() {
		return cell;
	}
	
	public void setCell(String cell) {
		this.cell = cell;
	}
	
	public String getLocationTime() {
		return locationTime;
	}
	
	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}
	
	public String getExpireDate() {
		return expireDate;
	}
	
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	
	public String getBatteryLevel() {
		return batteryLevel;
	}
	
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	
	public Boolean getOnLine() {
		return onLine;
	}
	
	public void setOnLine(Boolean onLine) {
		this.onLine = onLine;
	}
	
	public String getChargeStatus() {
		return chargeStatus;
	}
	
	public void setChargeStatus(String chargeStatus) {
		this.chargeStatus = chargeStatus;
	}
	
}
