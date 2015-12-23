package com.changhongit.loving.jpaRepository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.SOSSetting;

@RepositoryRestResource(exported = false)
public interface SOSSettingRepository extends
		CrudRepository<SOSSetting, String> {
	
}
