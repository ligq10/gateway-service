package com.changhongit.loving.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LastWarningFlag {
	
	@Id
	private String id;
	
	private boolean sosWarning = false;
	
	private boolean cellWarning = false;
	
	private boolean protectedCircle1Warning = false;
	
	private boolean protectedCircle2Warning = false;
	
	private boolean protectedCircle3Warning = false;
	
	private boolean protectedCircle4Warning = false;
	
	private boolean protectedCircle5Warning = false;
	
	public boolean isCellWarning() {
		return cellWarning;
	}
	
	public void setCellWarning(boolean cellWarning) {
		this.cellWarning = cellWarning;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isSosWarning() {
		return sosWarning;
	}
	
	public void setSosWarning(boolean sosWarning) {
		this.sosWarning = sosWarning;
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
	
}
