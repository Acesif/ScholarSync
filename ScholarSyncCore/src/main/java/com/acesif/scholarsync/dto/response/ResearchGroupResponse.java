package com.acesif.scholarsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResearchGroupResponse {

    private Long id;

    private String groupName;

    private String groupDescription;

    private String researchInterest;

    private List<ResearcherResponse> members;
}
