package com.changhongit.loving.model;

import java.io.Serializable;

public class ProtectedCircle implements Serializable {

	private int type;

	private String centreLongt = "";

	private String centreLat = "";

	private String radius = "";

	private String eastLongt = "";

	private String westLongt = "";

	private String southLat = "";

	private String northLat = "";

	private int contactFlag;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCentreLongt() {
		return centreLongt;
	}

	public void setCentreLongt(String centreLongt) {
		this.centreLongt = centreLongt;
	}

	public String getCentreLat() {
		return centreLat;
	}

	public void setCentreLat(String centreLat) {
		this.centreLat = centreLat;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getEastLongt() {
		return eastLongt;
	}

	public void setEastLongt(String eastLongt) {
		this.eastLongt = eastLongt;
	}

	public String getWestLongt() {
		return westLongt;
	}

	public void setWestLongt(String westLongt) {
		this.westLongt = westLongt;
	}

	public String getSouthLat() {
		return southLat;
	}

	public void setSouthLat(String southLat) {
		this.southLat = southLat;
	}

	public String getNorthLat() {
		return northLat;
	}

	public void setNorthLat(String northLat) {
		this.northLat = northLat;
	}

	public int getContactFlag() {
		return contactFlag;
	}

	public void setContactFlag(int contactFlag) {
		this.contactFlag = contactFlag;
	}

}
