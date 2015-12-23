package com.changhongit.loving.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.document.LastWarningFlag;

@RepositoryRestResource(exported = false)
public interface LastWarningFlagRepository extends
		CrudRepository<LastWarningFlag, String> {
	
}
