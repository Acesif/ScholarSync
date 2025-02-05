package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.ResearchPaper;
import org.springframework.stereotype.Service;

@Service
public class ResearchPaperService extends BaseService<ResearchPaper> {

    public ResearchPaperService(BaseRepository<ResearchPaper> baseRepository) {
        super(baseRepository);
    }
}
