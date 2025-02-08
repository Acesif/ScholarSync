package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.base.BaseUtil;
import com.acesif.scholarsync.dto.request.ResearchPaperRequest;
import com.acesif.scholarsync.dto.response.ResearchGroupResponse;
import com.acesif.scholarsync.dto.response.ResearchPaperResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.ResearchPaper;
import com.acesif.scholarsync.utils.mapper.ResearchPaperMapperUtil;
import com.acesif.scholarsync.utils.response.Response;
import com.acesif.scholarsync.utils.response.Status;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paper")
public class ResearchPaperController extends BaseController<ResearchPaper> {

    private final ResearchPaperMapperUtil researchPaperMapperUtil;

    public ResearchPaperController(BaseService<ResearchPaper> service, BaseUtil util, ResearchPaperMapperUtil researchPaperMapperUtil) {
        super(service, util);
        this.researchPaperMapperUtil = researchPaperMapperUtil;
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearchPaperResponse> save(@RequestBody ResearchPaperRequest researchPaperRequest) {
        ResearchPaper researchPaper = service.save(researchPaperMapperUtil.researchPaperRequestToResearchPaper(researchPaperRequest));
        return Response.<ResearchPaperResponse>builder()
                .status(Status.CREATED)
                .message("entity saved")
                .data(researchPaperMapperUtil.researchPaperToResearchPaperResponse(researchPaper))
                .build();
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearchPaperResponse> update(@RequestBody ResearchPaperRequest researchPaperRequest) {
        ResearchPaper researchPaper = service.update(researchPaperMapperUtil.researchPaperRequestToResearchPaper(researchPaperRequest));
        return Response.<ResearchPaperResponse>builder()
                .status(Status.UPDATED)
                .message("entity updated")
                .data(researchPaperMapperUtil.researchPaperToResearchPaperResponse(researchPaper))
                .build();
    }
}
