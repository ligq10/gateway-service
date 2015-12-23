package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TerminalStatus {
	
	@Id
	private String imei;
	
	private Date date;
	
	private Integer gpsStatus;
	
	private Float longitude;
	
	private Float latitude;
	
	private String mcc;
	
	private String mnc;
	
	private String lac;
	
	private String cell;
	
	private Integer batteralLevel;
	
	private String pedometer;
	
	private Float speed;
	
	private String groupUuid;
	
	private Date groupUuidUpdateTime;
	
	private Date expireDate;
	
	private String chargeStatus = "未充电";
	
	private boolean sosWarning = false;
	
	private boolean cellWarning = false;
	
	private boolean protectedCircle1Warning = false;
	
	private boolean protectedCircle2Warning = false;
	
	private boolean protectedCircle3Warning = false;
	
	private boolean protectedCircle4Warning = false;
	
	private boolean protectedCircle5Warning = false;
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
	
	public Integer getBatteralLevel() {
		return batteralLevel;
	}
	
	public void setBatteralLevel(Integer batteralLevel) {
		this.batteralLevel = batteralLevel;
	}
	
	public String getPedometer() {
		return pedometer;
	}
	
	public void setPedometer(String pedometer) {
		this.pedometer = pedometer;
	}
	
	public Float getSpeed() {
		return speed;
	}
	
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	
	public String getGroupUuid() {
		return groupUuid;
	}
	
	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}
	
	public Date getGroupUuidUpdateTime() {
		return groupUuidUpdateTime;
	}
	
	public void setGroupUuidUpdateTime(Date groupUuidUpdateTime) {
		this.groupUuidUpdateTime = groupUuidUpdateTime;
	}
	
	public boolean isSosWarning() {
		return sosWarning;
	}
	
	public void setSosWarning(boolean sosWarning) {
		this.sosWarning = sosWarning;
	}
	
	public boolean isCellWarning() {
		return cellWarning;
	}
	
	public void setCellWarning(boolean cellWarning) {
		this.cellWarning = cellWarning;
	}
	
	public boolean isProtectedCircle1Warning() {
		return protectedCircle1Warning;
	}
	
	public void setProtectedCircle1Warning(boolean protectedCircle1Warning) {
		this.protectedCircle1Warning = protectedCircle1Warning;
	}
	
	public boolean isProtectedCircle2Warning() {
		return protectedCircle2Warning;
	}
	
	public void setProtectedCircle2Warning(boolean protectedCircle2Warning) {
		this.protectedCircle2Warning = protectedCircle2Warning;
	}
	
	public boolean isProtectedCircle3Warning() {
		return protectedCircle3Warning;
	}
	
	public void setProtectedCircle3Warning(boolean protectedCircle3Warning) {
		this.protectedCircle3Warning = protectedCircle3Warning;
	}
	
	public boolean isProtectedCircle4Warning() {
		return protectedCircle4Warning;
	}
	
	public void setProtectedCircle4Warning(boolean protectedCircle4Warning) {
		this.protectedCircle4Warning = protectedCircle4Warning;
	}
	
	public boolean isProtectedCircle5Warning() {
		return protectedCircle5Warning;
	}
	
	public void setProtectedCircle5Warning(boolean protectedCircle5Warning) {
		this.protectedCircle5Warning = protectedCircle5Warning;
	}
	
	public String getChargeStatus() {
		return chargeStatus;
	}
	
	public void setChargeStatus(String chargeStatus) {
		this.chargeStatus = chargeStatus;
	}
	
	public Date getExpireDate() {
		return expireDate;
	}
	
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
}
