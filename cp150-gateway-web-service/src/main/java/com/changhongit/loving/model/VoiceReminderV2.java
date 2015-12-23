package com.changhongit.loving.model;

import java.io.Serializable;
import java.util.List;

public class VoiceReminderV2 implements Serializable {

    private Integer index;
    private Boolean active = true;
    private ReminderMode mode = ReminderMode.OneTime;
    private String reminderTime;
    private List<Boolean> repeatMode;
    private String content;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ReminderMode getMode() {
        return mode;
    }

    public void setMode(ReminderMode mode) {
        this.mode = mode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Boolean> getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(List<Boolean> repeatMode) {
        this.repeatMode = repeatMode;
    }
}
