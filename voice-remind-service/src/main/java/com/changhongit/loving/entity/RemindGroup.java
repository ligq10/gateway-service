package com.changhongit.loving.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class RemindGroup {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String name;
	
	private Date createTime = new Date();
	
	private int terminalCount;
	
	private String searchKeywords;
	
	private String ownerGroupId;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSearchKeywords() {
		return searchKeywords;
	}
	
	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	
	public int getTerminalCount() {
		return terminalCount;
	}
	
	public void setTerminalCount(int terminalCount) {
		this.terminalCount = terminalCount;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
