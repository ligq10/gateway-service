package com.changhongit.loving.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.changhongit.loving.entity.Group;

@Repository
public interface GroupRepository extends CrudRepository<Group, String> {
	
}
