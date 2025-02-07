package com.acesif.scholarsync.repository;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.entity.Researcher;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearcherRepository extends BaseRepository<Researcher> {

    Researcher findByEmail(String email);
}
