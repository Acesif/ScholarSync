package com.acesif.scholarsync.dto.request;

import com.acesif.scholarsync.entity.Researcher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingListRequest {

    private Long id;

    private String listName;

    private String listDescription;

    private ResearcherRequest researcher;

    private List<Long> researchPapers;
}
