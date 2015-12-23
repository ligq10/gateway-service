package com.changhongit.loving.repository;

import com.changhongit.loving.entity.ShortMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortMessageRepository extends CrudRepository<ShortMessage, String> {
}
