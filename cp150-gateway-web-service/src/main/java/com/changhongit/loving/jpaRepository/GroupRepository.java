package com.changhongit.loving.jpaRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.Group;

@RepositoryRestResource(exported = false)
public interface GroupRepository extends
		PagingAndSortingRepository<Group, String> {
	
}
