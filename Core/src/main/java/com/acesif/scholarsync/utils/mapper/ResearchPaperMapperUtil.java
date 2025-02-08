package com.acesif.scholarsync.utils.mapper;

import com.acesif.scholarsync.dto.request.ResearchPaperRequest;
import com.acesif.scholarsync.dto.response.ResearchPaperResponse;
import com.acesif.scholarsync.entity.ResearchPaper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResearchPaperMapperUtil extends GlobalUtil{

    public ResearchPaper researchPaperRequestToResearchPaper(ResearchPaperRequest researchPaperRequest) {
        return map(researchPaperRequest, ResearchPaper.class);
    }

    public ResearchPaperResponse researchPaperToResearchPaperResponse(ResearchPaper researchPaper) {
        return map(researchPaper, ResearchPaperResponse.class);
    }
}
