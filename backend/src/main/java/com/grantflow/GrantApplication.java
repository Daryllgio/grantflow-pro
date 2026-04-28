package com.grantflow;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User applicant;

    @ManyToOne(optional = false)
    private Program program;

    @Column(length = 3000)
    private String personalStatement;

    @Column(length = 2000)
    private String academicBackground;

    @Column(length = 2000)
    private String financialNeedStatement;

    @Column(length = 2000)
    private String leadershipExperience;

    @Column(length = 2000)
    private String communityImpact;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private boolean riskFlag;

    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
