package com.changhongit.loving.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.changhongit.loving.entity.TerminalUser;

@RepositoryRestResource(exported = false)
public interface TerminalUserRepository extends
		CrudRepository<TerminalUser, String> {
	
	TerminalUser findByTerminalImeiAndTerminalCheckCode(String imei,
			String checkCode);
	
	TerminalUser findByTerminalImei(String imei);
	
	@Query(value = "select new TerminalUser(tu.id, tu.telNum, tu.realName, tu.terminal) from TerminalUser tu join tu.medicalHistories h join tu.terminal t where tu.gender like %:gender% AND ( tu.terminalImei like %:keyword% or tu.realName like %:keyword% or tu.telNum like %:keyword% ) AND h.name in ( :medicalHistory ) AND t.groupId in ( :groupIds )")
	Page<TerminalUser> searchByKeywordAndGenderAndAgeAndMedicalHistory(
			@Param("keyword") String keyword, @Param("gender") String gender,
			@Param("medicalHistory") List<String> medicalHistory,
			@Param("groupIds") List<String> groupIds, Pageable pageable);
	
	@Query(value = "select new TerminalUser(tu.id, tu.telNum, tu.realName, tu.terminal) from TerminalUser tu join tu.terminal t where tu.gender like %:gender% AND ( tu.terminalImei like %:keyword% or tu.realName like %:keyword% or tu.telNum like %:keyword% ) AND t.groupId in ( :groupIds )")
	Page<TerminalUser> searchByKeywordAndGenderAndAge(
			@Param("keyword") String keyword, @Param("gender") String gender,
			@Param("groupIds") List<String> groupIds, Pageable pageable);
	
	@Query(value = "select new TerminalUser(tu.id, tu.telNum, tu.realName, tu.terminal) from TerminalUser tu join tu.medicalHistories h join tu.terminal t where tu.gender like %:gender% AND tu.birthday between :minBirthday and :maxBirthday AND ( tu.terminalImei like %:keyword% or tu.realName like %:keyword% or tu.telNum like %:keyword% ) and h.name in ( :medicalHistory ) AND t.groupId in ( :groupIds )")
	Page<TerminalUser> searchByKeywordAndGenderAndAgeAndMedicalHistoryAndBirthdayBetween(
			@Param("keyword") String keyword, @Param("gender") String gender,
			@Param("minBirthday") Date minBirthday,
			@Param("maxBirthday") Date maxBirthday,
			@Param("medicalHistory") List<String> medicalHistory,
			@Param("groupIds") List<String> groupIds, Pageable pageable);
	
	@Query(value = "select new TerminalUser(tu.id, tu.telNum, tu.realName, tu.terminal) from TerminalUser tu join tu.terminal t where tu.gender like %:gender% AND tu.birthday between :minBirthday and :maxBirthday AND ( tu.terminalImei like %:keyword% or tu.realName like %:keyword% or tu.telNum like %:keyword% ) AND t.groupId in ( :groupIds )")
	Page<TerminalUser> searchByKeywordAndGenderAndAgeAndBirthdayBetween(
			@Param("keyword") String keyword, @Param("gender") String gender,
			@Param("minBirthday") Date minBirthday,
			@Param("maxBirthday") Date maxBirthday,
			@Param("groupIds") List<String> groupIds, Pageable pageable);
	
}
