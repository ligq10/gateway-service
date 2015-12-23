package com.changhongit.loving.model;

import java.io.Serializable;

public class SettingContact implements Serializable {

	private Integer position;
	private String telNum = "";
	private String name = "";

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
