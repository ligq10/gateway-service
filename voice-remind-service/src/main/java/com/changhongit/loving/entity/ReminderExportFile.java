package com.changhongit.loving.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class ReminderExportFile {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String reminderId;
	
	@Column(columnDefinition = "BLOB", nullable = true)
	private byte[] exportFile;
	
	public String getId() {
		return id;
	}
	
	public String getReminderId() {
		return reminderId;
	}
	
	public void setReminderId(String reminderId) {
		this.reminderId = reminderId;
	}
	
	public byte[] getExportFile() {
		return exportFile;
	}
	
	public void setExportFile(byte[] exportFile) {
		this.exportFile = exportFile;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}
