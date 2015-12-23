package com.changhongit.loving.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class RemindGroupTerminal {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    private String remindGroupId;

    private String terminalId;

    public String getId() {
        return id;
    }

    public String getRemindGroupId() {
        return remindGroupId;
    }

    public void setRemindGroupId(String remindGroupId) {
        this.remindGroupId = remindGroupId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
