package com.acesif.scholarsync.utils.mapper;

import com.acesif.scholarsync.dto.request.ReadingListRequest;
import com.acesif.scholarsync.dto.request.ResearchGroupRequest;
import com.acesif.scholarsync.dto.response.ReadingListResponse;
import com.acesif.scholarsync.dto.response.ResearchGroupResponse;
import com.acesif.scholarsync.dto.response.ResearchPaperResponse;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.ResearchGroup;
import com.acesif.scholarsync.entity.ResearchPaper;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.repository.ResearchPaperRepository;
import com.acesif.scholarsync.repository.ResearcherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReadingListMapperUtil extends GlobalUtil{

    private final ResearchPaperRepository researchPaperRepository;

    public ReadingList readingListRequestToReadingList(ReadingListRequest readingListRequest) {

        List<ResearchPaper> researchPaperList = new ArrayList<>();
        if (readingListRequest.getResearchPapers() != null) {
            for (Long paper: readingListRequest.getResearchPapers()) {
                researchPaperList.add(researchPaperRepository.findById(paper).orElse(null));
            }
        }

        return ReadingList.builder()
                .id(readingListRequest.getId())
                .listName(readingListRequest.getListName())
                .listDescription(readingListRequest.getListDescription())
                .researcher(map(readingListRequest.getResearcher(), Researcher.class))
                .researchPapers(researchPaperList)
                .build();
    }

    public ReadingListResponse readingListToReadingListResponse(ReadingList readingList) {
        return ReadingListResponse.builder()
                .id(readingList.getId())
                .listName(readingList.getListName())
                .listDescription(readingList.getListDescription())
                .researcher(map(readingList.getResearcher(), Researcher.class))
                .researchPapers(mapList(readingList.getResearchPapers(), ResearchPaperResponse.class))
                .build();
    }
}
