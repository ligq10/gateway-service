package com.changhongit.loving.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
	
	private String lat;
	
	@JsonProperty(value = "lng")
	private String lon;
	
	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getLon() {
		return lon;
	}
	
	public void setLon(String lon) {
		this.lon = lon;
	}
	
}