package com.changhongit.loving.jpaRepository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.HeartBeat;

@RepositoryRestResource(exported = false)
public interface HeartbeatRepository extends CrudRepository<HeartBeat, String> {
	
	List<HeartBeat> findByImeiOrderByDateDesc(String imei, Pageable pageable);
}
