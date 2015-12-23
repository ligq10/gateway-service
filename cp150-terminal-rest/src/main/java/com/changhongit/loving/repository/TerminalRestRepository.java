package com.changhongit.loving.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.changhongit.loving.entity.Terminal;

@RepositoryRestResource(collectionResourceRel = "cp150s", path = "cp150s")
public interface TerminalRestRepository extends
		PagingAndSortingRepository<Terminal, String> {
	
	Page<Terminal> findByGroupId(@Param("groupid") String groupid,
			Pageable pageable);
	
	@RestResource(exported = false)
	List<Terminal> findByGroupId(String groupid);
	
	Terminal findByImeiAndCheckCode(@Param("imei") String imei,
			@Param("checkcode") String checkCode);
	
	Terminal findByImei(@Param("imei") String imei);
	
	@RestResource(exported = false)
	@Query(value = "select t from Terminal t where t.imei like %:keyword% or t.sim like %:keyword% or t.status like %:keyword% or t.ownerName like %:keyword%")
	Page<Terminal> findByKeyWord(@Param("keyword") String keyword,
			Pageable pageable);
	
	@RestResource(exported = false)
	@Query(value = "select t from Terminal t where (t.imei like %:keyword% or t.sim like %:keyword% or t.status like %:keyword% or t.ownerName like %:keyword% ) and t.groupId in ( :groupList )")
	Page<Terminal> findByKeyWordInGroup(@Param("keyword") String keyword,
			@Param("groupList") List<String> groupList, Pageable pageable);
	
	@RestResource(exported = false)
	Page<Terminal> findByGroupIdIn(List<String> groupList, Pageable pageable);
	
}
