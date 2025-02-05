package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.Group;
import org.springframework.stereotype.Service;

@Service
public class GroupService extends BaseService<Group> {

    public GroupService(BaseRepository<Group> baseRepository) {
        super(baseRepository);
    }
}
