package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.ResearchGroup;
import org.springframework.stereotype.Service;

@Service
public class ResearchGroupService extends BaseService<ResearchGroup> {

    public ResearchGroupService(BaseRepository<ResearchGroup> baseRepository) {
        super(baseRepository);
    }
}
