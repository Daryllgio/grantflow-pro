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
        app.setStatus(request.status());
        app.setUpdatedAt(LocalDateTime.now());

        auditLogRepository.save(AuditLog.builder()
                .actor(actor)
                .action("APPLICATION_STATUS_UPDATED")
                .entityType("APPLICATION")
                .entityId(app.getId())
                .details("Changed status from " + oldStatus + " to " + request.status())
                .createdAt(LocalDateTime.now())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(app.getApplicant())
                .title("Application status updated")
                .message("Your application status changed from " + oldStatus + " to " + request.status())
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
}
