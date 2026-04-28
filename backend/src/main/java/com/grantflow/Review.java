package com.grantflow;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private GrantApplication application;

    @ManyToOne(optional = false)
    private User reviewer;

    private Integer academicScore;
    private Integer leadershipScore;
    private Integer financialNeedScore;
    private Integer communityImpactScore;
    private Integer essayScore;
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    private Recommendation recommendation;

    @Column(length = 3000)
    private String feedback;

    private LocalDateTime reviewedAt;
}
