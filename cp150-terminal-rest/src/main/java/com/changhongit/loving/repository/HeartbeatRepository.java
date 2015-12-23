package com.changhongit.loving.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.HeartBeat;

@RepositoryRestResource(exported = false)
public interface HeartbeatRepository extends CrudRepository<HeartBeat, String> {
	
	Page<HeartBeat> findByImeiAndDateBetween(String imei, Date from, Date to,
			Pageable pageable);
}
