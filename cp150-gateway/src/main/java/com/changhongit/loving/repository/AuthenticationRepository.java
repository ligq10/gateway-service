package com.changhongit.loving.repository;

import com.changhongit.loving.entity.Authentication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends CrudRepository<Authentication, String> {
}
