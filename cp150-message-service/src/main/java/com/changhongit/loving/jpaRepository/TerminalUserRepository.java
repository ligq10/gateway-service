package com.changhongit.loving.jpaRepository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.TerminalUser;

@RepositoryRestResource(exported = false)
public interface TerminalUserRepository extends
		CrudRepository<TerminalUser, String> {
	
	TerminalUser findByTerminalImei(String imei);
	
}
