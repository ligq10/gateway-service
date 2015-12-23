package com.changhongit.loving.repository;

import com.changhongit.loving.entity.Terminal;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TerminalRepository extends
		PagingAndSortingRepository<Terminal, String> {

    List<Terminal> findByGroupId(String groupId);

}
