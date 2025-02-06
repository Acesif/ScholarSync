package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.ReadingList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/list")
public class ReadingListController extends BaseController<ReadingList> {

    public ReadingListController(BaseService<ReadingList> service) {
        super(service);
    }
}
