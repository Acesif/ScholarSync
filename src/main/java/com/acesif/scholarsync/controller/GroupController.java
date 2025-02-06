package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.Group;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group")
public class GroupController extends BaseController<Group> {

    public GroupController(BaseService<Group> service) {
        super(service);
    }
}
