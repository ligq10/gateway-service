package com.changhongit.loving.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.Group;

@RepositoryRestResource(exported = false)
public interface GroupRepository extends CrudRepository<Group, String> {
	
}
