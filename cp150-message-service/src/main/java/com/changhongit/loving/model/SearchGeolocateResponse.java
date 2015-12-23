package com.changhongit.loving.model;

public class SearchGeolocateResponse {
	
	private LovingLocation location;
	
	private String accuracy;
	
	public LovingLocation getLocation() {
		return location;
	}
	
	public void setLocation(LovingLocation location) {
		this.location = location;
	}
	
	public String getAccuracy() {
		return accuracy;
	}
	
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	
}
