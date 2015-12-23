package com.changhongit.loving.model;

import com.changhongit.loving.entity.RemindGroup;
import com.changhongit.loving.entity.RemindGroupTerminal;
import com.changhongit.loving.entity.Terminal;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindGroupResponse implements Page<Terminal>{

    private Page<RemindGroupTerminal> remindGroupTerminals;
    private RemindGroup remindGroup;
    private List<Terminal> terminals;

    public RemindGroupResponse(Page<RemindGroupTerminal> remindGroupTerminals, RemindGroup self, List<Terminal> terminals) {
        this.remindGroupTerminals = remindGroupTerminals;
        this.remindGroup = self;
        this.terminals = terminals;
    }

    public RemindGroup getRemindGroup() {
        return remindGroup;
    }

    public void setRemindGroup(RemindGroup remindGroup) {
        this.remindGroup = remindGroup;
    }

    @Override
    public int getTotalPages() {
        return remindGroupTerminals.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        return remindGroupTerminals.getTotalElements();
    }

    @Override
    public int getNumber() {
        return remindGroupTerminals.getNumber();
    }

    @Override
    public int getSize() {
        return remindGroupTerminals.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return remindGroupTerminals.getNumberOfElements();
    }

    @Override
    public List<Terminal> getContent() {
        return terminals;
    }

    @Override
    public boolean hasContent() {
        return remindGroupTerminals.hasNext();
    }

    @Override
    public Sort getSort() {
        return remindGroupTerminals.getSort();
    }

    @Override
    public boolean isFirst() {
        return remindGroupTerminals.isFirst();
    }

    @Override
    public boolean isLast() {
        return remindGroupTerminals.isLast();
    }

    @Override
    public boolean hasNext() {
        return remindGroupTerminals.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return remindGroupTerminals.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return null;
    }

    @Override
    public Pageable previousPageable() {
        return null;
    }

    @Override
    public Iterator<Terminal> iterator() {
        return null;
    }
}
