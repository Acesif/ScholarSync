package com.acesif.scholarsync.entity;

import com.acesif.scholarsync.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "researcher",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_researcher_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_researcher_email", columnNames = "email")
        })
public class Researcher extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String fullName;

    private String affiliation;

    @Column(nullable = false, unique = true)
    private String email;

    private String researchInterests;

    @OneToMany(mappedBy = "researcher", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @Column(nullable = false)
    private List<ReadingList> readingList;
}