package com.changhongit.loving.repository;

import com.changhongit.loving.entity.Warning;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningRepository extends CrudRepository<Warning, String> {

    List<Warning> findByImeiIn(List<String> imeis, Pageable pageable);

}
