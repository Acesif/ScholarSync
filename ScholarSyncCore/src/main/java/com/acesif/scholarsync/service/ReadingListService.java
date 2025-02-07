package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.ReadingList;
import org.springframework.stereotype.Service;

@Service
public class ReadingListService extends BaseService<ReadingList> {

    public ReadingListService(BaseRepository<ReadingList> baseRepository) {
        super(baseRepository);
    }
}
