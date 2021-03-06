package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.changhongit.loving.model.TerminalType;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "TERMINAL_CP150")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Terminal {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "guid")
	private String id;
	
	private String sn;
	
	private String modelNumber;
	
	private String imei;
	
	private TerminalType type = TerminalType.Cp150;
	
	private String sim;
	
	private String ownerName;
	
	private String checkCode;
	
	private String version;
	
	private String status = "未激活";
	
	@DateTimeFormat
	private Date activateTime;
	
	private String description;
	
	private String groupId = "guanhutong";
	
	public String getUuid() {
		return id;
	}
	
	public String getId() {
		return id;
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
	
	public TerminalType getType() {
		return type;
	}
	
	public void setType(TerminalType type) {
		this.type = type;
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
	
	public Date getActivateTime() {
		return activateTime;
	}
	
	public void setActivateTime(Date activateTime) {
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
	
	public Terminal() {
	}
	
	public Terminal(String id, String imei, String sim) {
		this.id = id;
		this.imei = imei;
		this.sim = sim;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Terminal terminal = (Terminal) o;
		
		if (id != null ? !id.equals(terminal.id) : terminal.id != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}
