package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController<User> {

    public UserController(BaseService<User> service) {
        super(service);
    }
}
