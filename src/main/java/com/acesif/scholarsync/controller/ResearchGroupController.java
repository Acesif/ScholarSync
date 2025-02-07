package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.base.BaseUtil;
import com.acesif.scholarsync.dto.request.ResearchGroupRequest;
import com.acesif.scholarsync.dto.response.ResearchGroupResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.ResearchGroup;
import com.acesif.scholarsync.utils.mapper.ResearchGroupMapperUtil;
import com.acesif.scholarsync.utils.response.Response;
import com.acesif.scholarsync.utils.response.Status;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group")
public class ResearchGroupController extends BaseController<ResearchGroup> {

    private final ResearchGroupMapperUtil researchGroupMapperUtil;

    public ResearchGroupController(BaseService<ResearchGroup> service, BaseUtil util, ResearchGroupMapperUtil researchGroupMapperUtil) {
        super(service, util);
        this.researchGroupMapperUtil = researchGroupMapperUtil;
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearchGroupResponse> save(@RequestBody ResearchGroupRequest researchGroupRequest) {
        ResearchGroup researchGroup = service.save(researchGroupMapperUtil.researchGroupRequestToResearchGroup(researchGroupRequest));
        return Response.<ResearchGroupResponse>builder()
                .status(Status.CREATED)
                .message("entity saved")
                .data(researchGroupMapperUtil.researchGroupToResearchGroupResponse(researchGroup))
                .build();
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearchGroupResponse> update(@RequestBody ResearchGroupRequest researchGroupRequest) {
        ResearchGroup researchGroup = service.update(researchGroupMapperUtil.researchGroupRequestToResearchGroup(researchGroupRequest));
        return Response.<ResearchGroupResponse>builder()
                .status(Status.UPDATED)
                .message("entity updated")
                .data(researchGroupMapperUtil.researchGroupToResearchGroupResponse(researchGroup))
                .build();
    }
}
