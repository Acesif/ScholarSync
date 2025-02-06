package com.acesif.scholarsync.entity;

import com.acesif.scholarsync.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "reading_list")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReadingList extends BaseEntity {

    @Column(nullable = false)
    private String listName;

    private String listDescription;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "reading_list_id"),
            inverseJoinColumns = @JoinColumn(name = "research_paper_id")
    )
    private List<ResearchPaper> researchPapers;
}
