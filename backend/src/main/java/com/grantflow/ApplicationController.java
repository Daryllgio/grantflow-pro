package com.grantflow;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public List<GrantApplication> all() {
        return applicationRepository.findAll();
    }

    @GetMapping("/my")
    public List<GrantApplication> mine(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        return applicationRepository.findByApplicantId(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public GrantApplication create(@RequestBody ApplicationDtos.CreateApplicationRequest request,
                                   HttpServletRequest http) {
        User user = (User) http.getAttribute("currentUser");

        Program program = programRepository.findById(request.programId())
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        GrantApplication app = GrantApplication.builder()
                .applicant(user)
                .program(program)
                .personalStatement(request.personalStatement())
                .academicBackground(request.academicBackground())
                .financialNeedStatement(request.financialNeedStatement())
                .leadershipExperience(request.leadershipExperience())
                .communityImpact(request.communityImpact())
                .status(ApplicationStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return applicationRepository.save(app);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('APPLICANT')")
    public GrantApplication updateDraft(@PathVariable Long id,
                                        @RequestBody ApplicationDtos.CreateApplicationRequest request,
                                        HttpServletRequest http) {
        User user = (User) http.getAttribute("currentUser");

        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!app.getApplicant().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot edit this application");
        }

        if (app.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalArgumentException("Only draft applications can be edited");
        }

        app.setPersonalStatement(request.personalStatement());
        app.setAcademicBackground(request.academicBackground());
        app.setFinancialNeedStatement(request.financialNeedStatement());
        app.setLeadershipExperience(request.leadershipExperience());
        app.setCommunityImpact(request.communityImpact());
        app.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(app);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('APPLICANT')")
    public GrantApplication submit(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");

        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!app.getApplicant().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot submit this application");
        }

        if (app.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalArgumentException("Only draft applications can be submitted");
        }

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        notificationRepository.save(Notification.builder()
                .recipient(user)
                .title("Application submitted")
                .message("Your application for " + app.getProgram().getName() + " was submitted successfully.")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        return applicationRepository.save(app);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public GrantApplication updateStatus(@PathVariable Long id,
                                         @RequestBody ApplicationDtos.UpdateStatusRequest request,
                                         HttpServletRequest http) {
        User actor = (User) http.getAttribute("currentUser");

        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        ApplicationStatus oldStatus = app.getStatus();

        if (oldStatus == request.status()) {
            return app;
        }

        app.setStatus(request.status());
        app.setUpdatedAt(LocalDateTime.now());

        auditLogRepository.save(AuditLog.builder()
                .actor(actor)
                .action("APPLICATION_STATUS_UPDATED")
                .entityType("APPLICATION")
                .entityId(app.getId())
                .details("Changed " + app.getProgram().getName() + " application from " + oldStatus + " to " + request.status())
                .createdAt(LocalDateTime.now())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(app.getApplicant())
                .title(app.getProgram().getName() + " status update")
                .message(statusMessage(app.getProgram().getName(), request.status()))
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        return applicationRepository.save(app);
    }

    @PostMapping("/{id}/documents")
    public ApplicationDocument addDocument(@PathVariable Long id,
                                           @RequestBody ApplicationDtos.AddDocumentRequest request) {
        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        ApplicationDocument doc = ApplicationDocument.builder()
                .application(app)
                .documentType(request.documentType())
                .fileName(request.fileName())
                .documentUrl(request.documentUrl())
                .storageKey("external-url")
                .uploadedAt(LocalDateTime.now())
                .build();

        return documentRepository.save(doc);
    }

    @GetMapping("/{id}/documents")
    public List<ApplicationDocument> documents(@PathVariable Long id) {
        return documentRepository.findByApplicationId(id);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Map<ApplicationStatus, Long> stats() {
        Map<ApplicationStatus, Long> stats = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            stats.put(status, applicationRepository.countByStatus(status));
        }
        return stats;
    }
    private String statusMessage(String programName, ApplicationStatus status) {
        return switch (status) {
            case SUBMITTED -> "Your application for " + programName + " was submitted successfully.";
            case ELIGIBILITY_SCREENING -> "Your application for " + programName + " is being checked for eligibility.";
            case UNDER_REVIEW -> "Your application for " + programName + " is now under review.";
            case INTERVIEW_STAGE -> "Your application for " + programName + " has moved to the interview stage.";
            case FINAL_DECISION -> "A final decision is being prepared for your " + programName + " application.";
            case APPROVED -> "Congratulations. Your application for " + programName + " was approved.";
            case REJECTED -> "Your application for " + programName + " was reviewed and was not selected.";
            case WAITLISTED -> "Your application for " + programName + " has been placed on the waitlist.";
            case WITHDRAWN -> "Your application for " + programName + " was withdrawn.";
            default -> "Your application for " + programName + " was updated.";
        };
    }

}
