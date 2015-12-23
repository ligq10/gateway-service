package com.changhongit.loving.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class ReminderTerminal {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String reminderId;
	
	private String terminalId;
	
	private boolean status = true;
	
	public String getId() {
		return id;
	}
	
	public String getReminderId() {
		return reminderId;
	}
	
	public void setReminderId(String reminderId) {
		this.reminderId = reminderId;
	}
	
	public String getTerminalId() {
		return terminalId;
	}
	
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	
	public boolean isStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
