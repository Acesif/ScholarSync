package com.acesif.scholarsync.service;

import com.acesif.scholarsync.base.BaseRepository;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User> {

    public UserService(BaseRepository<User> baseRepository) {
        super(baseRepository);
    }
}
