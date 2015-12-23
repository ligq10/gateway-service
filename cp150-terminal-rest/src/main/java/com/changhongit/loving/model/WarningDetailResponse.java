package com.changhongit.loving.model;

public class WarningDetailResponse {
	
	private String id;
	
	private String origin;
	
	private String imei;
	
	private String name;
	
	private String warning;
	
	private String date;
	
	private String type;
	
	private Integer gpsStatus;
	
	private Float longitude;
	
	private Float latitude;
	
	private String mcc;
	
	private String mnc;
	
	private String lac;
	
	private String cell;
	
	private Integer batteralLevel;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getWarning() {
		return warning;
	}
	
	public void setWarning(String warning) {
		this.warning = warning;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getImei() {
		return imei;
	}
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
}
