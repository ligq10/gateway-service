package com.changhongit.loving.repository;

import com.changhongit.loving.entity.MedicalHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface MedicalHistoryRepository extends CrudRepository<MedicalHistory, String> {
}
