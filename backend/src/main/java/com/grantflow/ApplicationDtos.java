package com.grantflow;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ApplicationDtos {
    public record CreateProgramRequest(
            String name,
            String description,
            BigDecimal awardAmount,
            LocalDate applicationDeadline,
            String eligibilityCriteria
    ) {}

    public record CreateApplicationRequest(
            Long programId,
            String personalStatement,
            String academicBackground,
            String financialNeedStatement,
            String leadershipExperience,
            String communityImpact
    ) {}

    public record UpdateStatusRequest(ApplicationStatus status) {}

    public record AddDocumentRequest(
            DocumentType documentType,
            String fileName,
            String documentUrl
    ) {}

    public record AssignReviewerRequest(Long reviewerId) {}

    public record ReviewRequest(
            Integer academicScore,
            Integer leadershipScore,
            Integer financialNeedScore,
            Integer communityImpactScore,
            Integer essayScore,
            Recommendation recommendation,
            String feedback
    ) {}
}
