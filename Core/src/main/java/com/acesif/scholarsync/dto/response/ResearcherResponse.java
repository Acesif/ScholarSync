package com.acesif.scholarsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResearcherResponse {

    private Long id;

    private String username;

    private String fullName;

    private String affiliation;

    private String email;

    private String researchInterests;

    private List<ReadingListResponse> readingList;
}
