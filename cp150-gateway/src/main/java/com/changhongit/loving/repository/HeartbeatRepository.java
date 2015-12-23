package com.changhongit.loving.repository;

import com.changhongit.loving.entity.HeartBeat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartbeatRepository extends CrudRepository<HeartBeat, String> {
}
