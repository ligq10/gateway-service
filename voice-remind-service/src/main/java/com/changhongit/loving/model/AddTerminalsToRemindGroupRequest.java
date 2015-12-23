package com.changhongit.loving.model;

import java.util.List;

public class AddTerminalsToRemindGroupRequest {

    private List<String> groupIds;
    private List<String> terminalIds;
    private String searchParams;
    private List<String> searchKeywords;

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public List<String> getTerminalIds() {
        return terminalIds;
    }

    public void setTerminalIds(List<String> terminalId) {
        this.terminalIds = terminalId;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
