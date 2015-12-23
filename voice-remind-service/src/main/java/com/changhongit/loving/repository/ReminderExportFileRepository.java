package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.ReminderExportFile;

@Repository
public interface ReminderExportFileRepository extends
		PagingAndSortingRepository<ReminderExportFile, String> {
	
	List<ReminderExportFile> findByReminderId(String reminderId);
}
