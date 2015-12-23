package com.changhongit.loving.jpaRepository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.MessagesToCp150;

@RepositoryRestResource(exported = false)
public interface MessagesToCp150Repository extends
		CrudRepository<MessagesToCp150, Long> {
	
	MessagesToCp150 findByImeiAndSeq(String imei, String seq);
	
	List<MessagesToCp150> findByImei(Integer retryTimes);
	
}
