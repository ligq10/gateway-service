package com.changhongit.loving.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.Reminder;

@Repository
public interface ReminderRepository extends
		PagingAndSortingRepository<Reminder, String> {
	
	Reminder findById(String id);
	
	Page<Reminder> findByOwnerGroupIdIn(List<String> groupList,
			Pageable pageable);
	
	Page<Reminder> findByNeedIssueAndReminderTimeLessThanAndOwnerGroupIdInOrderByReminderTimeDesc(
			boolean needIssue, Date date, List<String> groupList,
			Pageable pageable);
	
	Page<Reminder> findByNeedIssueOrReminderTimeGreaterThanAndOwnerGroupIdInOrderByReminderTimeDesc(
			boolean needIssue, Date date, List<String> groupList,
			Pageable pageable);
	
	List<Reminder> findByNeedIssue(boolean needIssue);
	
	List<Reminder> findByNeedExport(boolean needExport);
}
