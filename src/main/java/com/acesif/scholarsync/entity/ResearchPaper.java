package com.acesif.scholarsync.entity;

import com.acesif.scholarsync.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "research_paper")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResearchPaper extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String abstractText;

    private String publicationDate;

    private String journal;

    @Column(nullable = false)
    private String doi;

    @Column(nullable = false)
    private String url;
}
