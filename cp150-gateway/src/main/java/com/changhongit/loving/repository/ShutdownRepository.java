package com.changhongit.loving.repository;

import com.changhongit.loving.entity.Shutdown;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShutdownRepository extends CrudRepository<Shutdown, String> {
}
