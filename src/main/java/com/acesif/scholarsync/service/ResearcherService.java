package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.repository.ResearcherRepository;
import org.springframework.stereotype.Service;

@Service
public class ResearcherService extends BaseService<Researcher> {

    private final ResearcherRepository researcherRepository;

    public ResearcherService(BaseRepository<Researcher> baseRepository, ResearcherRepository researcherRepository) {
        super(baseRepository);
        this.researcherRepository = researcherRepository;
    }

    public Researcher findByEmail(String email) {
        return researcherRepository.findByEmail(email);
    }
}
