package com.acesif.scholarsync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResearcherRequest {

    private Long id;

    private String username;

    private String fullName;

    private String affiliation;

    private String email;

    private String researchInterests;

    private List<Long> readingList;
}
