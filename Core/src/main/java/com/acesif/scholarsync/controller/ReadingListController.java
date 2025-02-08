package com.acesif.scholarsync.controller;

import com.acesif.scholarsync.base.BaseController;
import com.acesif.scholarsync.base.BaseService;
import com.acesif.scholarsync.base.BaseUtil;
import com.acesif.scholarsync.dto.request.ReadingListRequest;
import com.acesif.scholarsync.dto.request.ResearcherRequest;
import com.acesif.scholarsync.dto.response.ReadingListResponse;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.utils.mapper.ReadingListMapperUtil;
import com.acesif.scholarsync.utils.response.Response;
import com.acesif.scholarsync.utils.response.Status;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/list")
public class ReadingListController extends BaseController<ReadingList> {

    private final ReadingListMapperUtil readingListMapperUtil;

    public ReadingListController(BaseService<ReadingList> service, BaseUtil util, ReadingListMapperUtil readingListMapperUtil) {
        super(service, util);
        this.readingListMapperUtil = readingListMapperUtil;
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ReadingListResponse> save(@RequestBody ReadingListRequest readingListRequest) {
        ReadingList readingList = service.save(readingListMapperUtil.readingListRequestToReadingList(readingListRequest));
        return Response.<ReadingListResponse>builder()
                .status(Status.CREATED)
                .message("entity saved")
                .data(readingListMapperUtil.readingListToReadingListResponse(readingList))
                .build();
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ReadingListResponse> update(@RequestBody ReadingListRequest readingListRequest) {
        ReadingList readingList = service.update(readingListMapperUtil.readingListRequestToReadingList(readingListRequest));
        return Response.<ReadingListResponse>builder()
                .status(Status.UPDATED)
                .message("entity updated")
                .data(readingListMapperUtil.readingListToReadingListResponse(readingList))
                .build();
    }
}
