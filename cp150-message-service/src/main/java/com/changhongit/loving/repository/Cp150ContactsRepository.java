package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.document.Cp150Contacts;

@RepositoryRestResource(exported = false)
public interface Cp150ContactsRepository extends
		CrudRepository<Cp150Contacts, String> {
	
	Cp150Contacts findByImei(String imei);
	
	@Query("{'$or':[{'contacts.telNum':{$regex : ?0}},{'contacts.telNum':{$regex : ?0}},{'whiteList.telNum':{$regex : ?0}}]}")
	List<Cp150Contacts> findByContactsTelNum(String contactTel);
	
}
