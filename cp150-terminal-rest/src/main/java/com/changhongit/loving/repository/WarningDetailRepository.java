package com.changhongit.loving.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.WarningDetail;

@RepositoryRestResource(exported = false)
public interface WarningDetailRepository extends
		CrudRepository<WarningDetail, String> {
	
	Page<WarningDetail> findByImeiInOrderByImeiDesc(List<String> imeis,
			Pageable pageable);
	
	Page<WarningDetail> findByImeiAndTypeAndDateBetween(String imei,
			String type, Date start, Date end, Pageable pageable);
	
	Page<WarningDetail> findByImeiAndDateBetween(String imei, Date start,
			Date end, Pageable pageable);
}
