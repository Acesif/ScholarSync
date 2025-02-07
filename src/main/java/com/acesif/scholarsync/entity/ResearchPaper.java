package com.acesif.scholarsync.entity;

import com.acesif.scholarsync.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(
        name = "research_paper",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doi", columnNames = "doi")
        }
)
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
