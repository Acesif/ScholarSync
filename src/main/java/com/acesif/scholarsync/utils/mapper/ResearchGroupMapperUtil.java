package com.acesif.scholarsync.utils.mapper;

import com.acesif.scholarsync.dto.request.ResearchGroupRequest;
import com.acesif.scholarsync.dto.response.ResearchGroupResponse;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ResearchGroup;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.repository.ResearcherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResearchGroupMapperUtil extends GlobalUtil{

    private final ResearcherRepository researcherRepository;

    public ResearchGroup researchGroupRequestToResearchGroup(ResearchGroupRequest researchGroupRequest) {

        List<Researcher> researchers = new ArrayList<>();
        if (researchGroupRequest.getMembers() != null) {
            for (Long item: researchGroupRequest.getMembers()){
                researchers.add(
                        researcherRepository.findById(item).orElse(null)
                );
            }
        }

        return ResearchGroup.builder()
                .groupName(researchGroupRequest.getGroupName())
                .groupDescription(researchGroupRequest.getGroupDescription())
                .researchInterest(researchGroupRequest.getResearchInterest())
                .members(researchers)
                .build();
    }

    public ResearchGroupResponse researchGroupToResearchGroupResponse(ResearchGroup researchGroup) {
        return ResearchGroupResponse.builder()
                .id(researchGroup.getId())
                .groupName(researchGroup.getGroupName())
                .groupDescription(researchGroup.getGroupDescription())
                .researchInterest(researchGroup.getResearchInterest())
                .members(mapList(researchGroup.getMembers(), ResearcherResponse.class))
                .build();
    }
}
