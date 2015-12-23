package com.changhongit.loving.jpaRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.Terminal;

@RepositoryRestResource(exported = false)
public interface TerminalRepository extends
		PagingAndSortingRepository<Terminal, String> {
	
	Terminal findByImei(String imei);
	
}
