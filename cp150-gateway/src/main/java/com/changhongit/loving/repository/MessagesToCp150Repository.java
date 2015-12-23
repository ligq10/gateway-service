package com.changhongit.loving.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.changhongit.loving.entity.MessagesToCp150;

public interface MessagesToCp150Repository extends
		CrudRepository<MessagesToCp150, Long> {
	
	MessagesToCp150 findByImeiAndSeq(String imei, String seq);
	
	Page<MessagesToCp150> findByImei(String imei, Pageable pageable);
	
}
