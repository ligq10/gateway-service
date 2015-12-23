package com.changhongit.loving.repository;

import com.changhongit.loving.entity.Warning;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarningRepository extends CrudRepository<Warning, String> {
}
