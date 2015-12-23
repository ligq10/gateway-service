package com.changhongit.loving.model;

import java.io.Serializable;

public class Call implements Serializable {

	private String telNum;
	private int type;

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
