package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.msgpack.annotation.Message;

@Entity
@Table(name = "WARNING_DETAIL")
@Message
public class WarningDetail {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "guid")
	private String id;
	
	private String origin;
	
	private String imei;
	
	private String owner;
	
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
	
	private String type;
	
	private String content;
	
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
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
}
