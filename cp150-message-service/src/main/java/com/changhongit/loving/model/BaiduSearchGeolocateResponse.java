package com.changhongit.loving.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduSearchGeolocateResponse {
	
	private int status;
	
	private Result result;
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Result {
		
		private Location location;
		
		private String formatted_address;
		
		private String business;
		
		public String getFormatted_address() {
			return formatted_address;
		}
		
		public void setFormatted_address(String formatted_address) {
			this.formatted_address = formatted_address;
		}
		
		public Location getLocation() {
			return location;
		}
		
		public void setLocation(Location location) {
			this.location = location;
		}
		
		public String getBusiness() {
			return business;
		}
		
		public void setBusiness(String business) {
			this.business = business;
		}
		
	}
	
}
