package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.base.BaseUtil;
import com.acesif.scholarsync.dto.request.ResearcherRequest;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.service.ResearcherService;
import com.acesif.scholarsync.utils.mapper.ResearcherMapperUtil;
import com.acesif.scholarsync.utils.response.Response;
import com.acesif.scholarsync.utils.response.Status;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ResearcherController extends BaseController<Researcher> {
    
    private final ResearcherMapperUtil researcherMapperUtil;
    private final ResearcherService researcherService;

    public ResearcherController(BaseService<Researcher> service, BaseUtil util, ResearcherMapperUtil researcherMapperUtil, ResearcherService researcherService) {
        super(service, util);
        this.researcherMapperUtil = researcherMapperUtil;
        this.researcherService = researcherService;
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearcherResponse> save(@RequestBody ResearcherRequest researcherRequest) {
        if (researcherService.findByEmail(researcherRequest.getEmail()) == null) {
            Researcher researcher = service.save(researcherMapperUtil.researcherRequestToResearcher(researcherRequest));
            return Response.<ResearcherResponse>builder()
                    .status(Status.CREATED)
                    .message("entity saved")
                    .data(researcherMapperUtil.researcherToResearcherResponse(researcher))
                    .build();
        } else {
            return Response.<ResearcherResponse>builder()
                    .status(Status.DUPLICATE)
                    .message("User with this email already exists")
                    .data(null)
                    .build();
        }
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ResearcherResponse> update(@RequestBody ResearcherRequest researcherRequest) {

        Researcher find = service.findById(researcherRequest.getId());
        researcherRequest.setUsername(find.getUsername());
        researcherRequest.setEmail(find.getEmail());

        Researcher researcher = service.update(researcherMapperUtil.researcherRequestToResearcher(researcherRequest));
        if (researcher == null) {
            return Response.<ResearcherResponse>builder()
                    .status(Status.NO_DATA)
                    .message("entity could not be updated")
                    .data(null)
                    .build();
        }
        return Response.<ResearcherResponse>builder()
                .status(Status.UPDATED)
                .message("entity updated")
                .data(researcherMapperUtil.researcherToResearcherResponse(researcher))
                .build();
    }
}
