package com.acesif.scholarsync.utils.mapper;

import com.acesif.scholarsync.dto.request.ResearcherRequest;
import com.acesif.scholarsync.dto.response.ReadingListResponse;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.repository.ReadingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResearcherMapperUtil extends GlobalUtil{

    private final ReadingListRepository readingListRepository;

    public Researcher researcherRequestToResearcher(ResearcherRequest researcherRequest) {

        List<ReadingList> readingLists = new ArrayList<>();
        if (researcherRequest.getReadingList() != null) {
            for (Long item: researcherRequest.getReadingList()){
                readingLists.add(readingListRepository.findById(item).orElse(null));
            }
        }

        return Researcher.builder()
                .id(researcherRequest.getId() == null ? null : researcherRequest.getId())
                .username(researcherRequest.getUsername())
                .fullName(researcherRequest.getFullName())
                .affiliation(researcherRequest.getAffiliation())
                .email(researcherRequest.getEmail())
                .researchInterests(researcherRequest.getResearchInterests())
                .readingList(readingLists)
                .build();
    }

    public ResearcherResponse researcherToResearcherResponse(Researcher researcher) {
        return ResearcherResponse.builder()
                .id(researcher.getId())
                .username(researcher.getUsername())
                .fullName(researcher.getFullName())
                .affiliation(researcher.getAffiliation())
                .email(researcher.getEmail())
                .researchInterests(researcher.getResearchInterests())
                .readingList(mapList(researcher.getReadingList(), ReadingListResponse.class))
                .build();
    }
}
