package com.changhongit.loving.model;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.changhongit.loving.entity.Reminder;
import com.changhongit.loving.entity.ReminderTerminal;
import com.changhongit.loving.entity.Terminal;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReminderResponse implements Page<Terminal> {
	
	private Page<ReminderTerminal> reminderTerminals;
	
	private Reminder reminder;
	
	private List<Terminal> terminals;
	
	public ReminderResponse(Page<ReminderTerminal> reminderTerminals,
			Reminder self, List<Terminal> terminals) {
		this.reminderTerminals = reminderTerminals;
		this.reminder = self;
		this.terminals = terminals;
	}
	
	public Reminder getReminder() {
		return reminder;
	}
	
	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}
	
	@Override
	public int getTotalPages() {
		return reminderTerminals.getTotalPages();
	}
	
	@Override
	public long getTotalElements() {
		return reminderTerminals.getTotalElements();
	}
	
	@Override
	public int getNumber() {
		return reminderTerminals.getNumber();
	}
	
	@Override
	public int getSize() {
		return reminderTerminals.getSize();
	}
	
	@Override
	public int getNumberOfElements() {
		return reminderTerminals.getNumberOfElements();
	}
	
	@Override
	public List<Terminal> getContent() {
		return terminals;
	}
	
	@Override
	public boolean hasContent() {
		return reminderTerminals.hasNext();
	}
	
	@Override
	public Sort getSort() {
		return reminderTerminals.getSort();
	}
	
	@Override
	public boolean isFirst() {
		return reminderTerminals.isFirst();
	}
	
	@Override
	public boolean isLast() {
		return reminderTerminals.isLast();
	}
	
	@Override
	public boolean hasNext() {
		return reminderTerminals.hasNext();
	}
	
	@Override
	public boolean hasPrevious() {
		return reminderTerminals.hasPrevious();
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
