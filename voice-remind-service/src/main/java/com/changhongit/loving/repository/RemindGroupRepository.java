package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.RemindGroup;

@Repository
public interface RemindGroupRepository extends
		PagingAndSortingRepository<RemindGroup, String> {
	
	RemindGroup findById(String id);
	
	Page<RemindGroup> findByOwnerGroupIdInOrderByCreateTimeDesc(
			List<String> groupList, Pageable pageable);
}
