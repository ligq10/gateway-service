package com.changhongit.loving.jpaRepository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.TerminalStatus;

@RepositoryRestResource(exported = false)
public interface TerminalStatusRepository extends
		PagingAndSortingRepository<TerminalStatus, String> {
	
	@Override
	<S extends TerminalStatus> S save(S entity);
	
	Page<TerminalStatus> findByGroupUuidInAndDateAfter(List<String> groupUuids,
			Date date, Pageable pageable);
	
	long countByGroupUuidInAndDateAfter(List<String> groupUuids, Date date);
	
	long countByGroupUuidAndDateAfter(String groupId, Date date);
	
	long countByGroupUuidIn(List<String> groupUuids);
	
	long countByGroupUuid(String groupId);
	
	@Query(value = "select count(*) from TerminalStatus ts where (ts.sosWarning=true or ts.cellWarning=true or ts.protectedCircle1Warning=true or ts.protectedCircle2Warning=true or ts.protectedCircle3Warning=true or ts.protectedCircle4Warning=true or ts.protectedCircle5Warning=true) AND ts.groupUuid in ( :groupIds )")
	long countByGroupUuidInOrWarningFlageIsTrue(
			@Param("groupIds") List<String> groupIds);
	
	@Query(value = "select count(*) from TerminalStatus ts where (ts.sosWarning=true or ts.cellWarning=true or ts.protectedCircle1Warning=true or ts.protectedCircle2Warning=true or ts.protectedCircle3Warning=true or ts.protectedCircle4Warning=true or ts.protectedCircle5Warning=true) AND ts.groupUuid = ( :groupId )")
	long countByGroupUuidOrWarningFlageIsTrue(@Param("groupId") String groupId);
	
}
