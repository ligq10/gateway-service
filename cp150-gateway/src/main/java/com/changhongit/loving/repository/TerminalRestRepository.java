package com.changhongit.loving.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.changhongit.loving.entity.Terminal;

public interface TerminalRestRepository extends
		PagingAndSortingRepository<Terminal, String> {
	
	Terminal findByImei(String imei);
	
}
