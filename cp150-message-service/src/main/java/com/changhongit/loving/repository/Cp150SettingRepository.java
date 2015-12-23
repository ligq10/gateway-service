package com.changhongit.loving.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.document.Cp150Setting;

@RepositoryRestResource(exported = false)
public interface Cp150SettingRepository extends
		CrudRepository<Cp150Setting, String> {
	
	Cp150Setting findByImeiAndKey(String imei, String key);
	
}
