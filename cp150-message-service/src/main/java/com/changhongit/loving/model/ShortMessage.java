package com.changhongit.loving.model;

import java.io.Serializable;

public class ShortMessage implements Serializable {

	private String telNum;
	private String content;

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
