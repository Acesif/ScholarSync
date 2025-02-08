package com.acesif.scholarsync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResearchGroupRequest {

    private Long id;

    private String groupName;

    private String groupDescription;

    private String researchInterest;

    private List<Long> members;
}
