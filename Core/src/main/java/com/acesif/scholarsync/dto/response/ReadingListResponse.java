package com.acesif.scholarsync.dto.response;

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
public class ReadingListResponse {

    private Long id;

    private String listName;

    private String listDescription;

    private Researcher researcher;

    private List<ResearchPaperResponse> researchPapers;
}
