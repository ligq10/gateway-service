package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.ReminderTerminal;

@Repository
public interface ReminderTerminalRepository extends
		PagingAndSortingRepository<ReminderTerminal, String> {
	
	int countByReminderId(String reminderId);
	
	List<ReminderTerminal> findByStatus(boolean status);
	
	List<ReminderTerminal> findByReminderId(String reminderId);
	
	Page<ReminderTerminal> findByReminderId(String reminderId, Pageable pageable);
	
	ReminderTerminal findByReminderIdAndTerminalId(String reminderId,
			String terminalId);
	
}
