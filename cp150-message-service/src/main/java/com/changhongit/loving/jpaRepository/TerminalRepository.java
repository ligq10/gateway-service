package com.changhongit.loving.jpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.Terminal;

@RepositoryRestResource(exported = false)
public interface TerminalRepository extends
		PagingAndSortingRepository<Terminal, String> {
	
	Terminal findByImei(String imei);
	
	@Query(value = "select t from Terminal t where (t.imei like %:keyword% or t.sim like %:keyword% or t.status like %:keyword% or t.ownerName like %:keyword% ) and t.groupId in ( :groupList )")
	Page<Terminal> findByKeyWordInGroup(@Param("keyword") String keyword,
			@Param("groupList") List<String> groupList, Pageable pageable);
	
	@Query(value = "select t from Terminal t where (t.sim = :sim or t.imei in ( :imeis )) and t.groupId in ( :groupList )")
	Page<Terminal> findBySimAndInImeiAndInGroup(@Param("sim") String sim,
			@Param("imeis") List<String> imeis,
			@Param("groupList") List<String> groupList, Pageable pageable);
	
	@Query(value = "select t from Terminal t where (t.sim = :sim ) and t.groupId in ( :groupList )")
	Page<Terminal> findBySimAndInGroup(@Param("sim") String sim,
			@Param("groupList") List<String> groupList, Pageable pageable);
}
