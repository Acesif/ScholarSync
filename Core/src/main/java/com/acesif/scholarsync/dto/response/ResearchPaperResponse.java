package com.acesif.scholarsync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResearchPaperResponse {

    private Long id;

    private String title;

    private String abstractText;

    private String publicationDate;

    private String journal;

    private String doi;

    private String url;
}
