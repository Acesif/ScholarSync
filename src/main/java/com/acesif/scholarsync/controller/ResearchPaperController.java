package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.entity.ResearchPaper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paper")
public class ResearchPaperController extends BaseController<ResearchPaper> {

    public ResearchPaperController(BaseService<ResearchPaper> service) {
        super(service);
    }
}
