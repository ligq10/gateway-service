package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.RemindGroupTerminal;

@Repository
public interface RemindGroupTerminalRepository extends
		PagingAndSortingRepository<RemindGroupTerminal, String> {
	
	int countByRemindGroupId(String remindGroupId);
	
	List<RemindGroupTerminal> findByRemindGroupId(String remindGroupId);
	
	Page<RemindGroupTerminal> findByRemindGroupId(String remindGroupId,
			Pageable pageable);
	
	RemindGroupTerminal findByRemindGroupIdAndTerminalId(String remindGroupId,
			String terminalId);
	
}
