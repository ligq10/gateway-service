package com.changhongit.loving.model;

import java.util.List;

public class ReminderExportsResponse {
	
	private int count;
	
	private int listSize;
	
	private List<String> reminderExports;
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public List<String> getReminderExports() {
		return reminderExports;
	}
	
	public void setReminderExports(List<String> reminderExports) {
		this.reminderExports = reminderExports;
	}
	
	public int getListSize() {
		return listSize;
	}
	
	public void setListSize(int listSize) {
		this.listSize = listSize;
	}
	
}
