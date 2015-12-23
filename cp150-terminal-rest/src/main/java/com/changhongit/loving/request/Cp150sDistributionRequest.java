package com.changhongit.loving.request;

import java.util.List;

import com.changhongit.loving.UtilValue;

public class Cp150sDistributionRequest {

	private List<String> fromGroupIds;

	private List<String> fromTerminalIds;

	private String toGroupId = UtilValue.GHT_ID;

	public List<String> getFromGroupIds() {
		return fromGroupIds;
	}

	public void setFromGroupIds(List<String> fromGroupIds) {
		this.fromGroupIds = fromGroupIds;
	}

	public List<String> getFromTerminalIds() {
		return fromTerminalIds;
	}

	public void setFromTerminalIds(List<String> fromTerminalIds) {
		this.fromTerminalIds = fromTerminalIds;
	}

	public String getToGroupId() {
		return toGroupId;
	}

	public void setToGroupId(String toGroupId) {
		this.toGroupId = toGroupId;
	}

}
