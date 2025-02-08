package com.acesif.scholarsync.entity;

import com.acesif.scholarsync.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "research_group")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResearchGroup extends BaseEntity {

    @Column(nullable = false)
    private String groupName;

    private String groupDescription;

    private String researchInterest;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "researcher_id")
    )

    private List<Researcher> members;
}
