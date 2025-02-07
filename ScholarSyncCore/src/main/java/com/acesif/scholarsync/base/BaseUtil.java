package com.acesif.scholarsync.base;

import com.acesif.scholarsync.dto.response.ReadingListResponse;
import com.acesif.scholarsync.dto.response.ResearchGroupResponse;
import com.acesif.scholarsync.dto.response.ResearchPaperResponse;
import com.acesif.scholarsync.dto.response.ResearcherResponse;
import com.acesif.scholarsync.entity.ReadingList;
import com.acesif.scholarsync.entity.ResearchGroup;
import com.acesif.scholarsync.entity.ResearchPaper;
import com.acesif.scholarsync.entity.Researcher;
import com.acesif.scholarsync.utils.mapper.ReadingListMapperUtil;
import com.acesif.scholarsync.utils.mapper.ResearchGroupMapperUtil;
import com.acesif.scholarsync.utils.mapper.ResearchPaperMapperUtil;
import com.acesif.scholarsync.utils.mapper.ResearcherMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class BaseUtil {

    private final ResearcherMapperUtil researcherMapperUtil;
    private final ReadingListMapperUtil readingListMapperUtil;
    private final ResearchGroupMapperUtil researchGroupMapperUtil;
    private final ResearchPaperMapperUtil researchPaperMapperUtil;

    public Object getDTO(Object entity) {
        if (entity instanceof Researcher researcher) {
            return researcherMapperUtil.researcherToResearcherResponse(researcher);
        } else if (entity instanceof ResearchGroup researchGroup) {
            return researchGroupMapperUtil.researchGroupToResearchGroupResponse(researchGroup);
        } else if (entity instanceof ResearchPaper researchPaper) {
            return researchPaperMapperUtil.researchPaperToResearchPaperResponse(researchPaper);
        } else if (entity instanceof ReadingList readingList) {
            return readingListMapperUtil.readingListToReadingListResponse(readingList);
        }

        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getSimpleName());
    }
}
