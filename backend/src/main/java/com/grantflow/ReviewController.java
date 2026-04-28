package com.grantflow;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewAssignmentRepository assignmentRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    @PostMapping("/applications/{id}/assign-reviewer")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public ReviewAssignment assignReviewer(@PathVariable Long id,
                                           @RequestBody ApplicationDtos.AssignReviewerRequest request,
                                           HttpServletRequest http) {
        User actor = (User) http.getAttribute("currentUser");

        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        User reviewer = userRepository.findById(request.reviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        if (reviewer.getRole() != Role.REVIEWER) {
            throw new IllegalArgumentException("Assigned user must be a reviewer");
        }

        app.setStatus(ApplicationStatus.UNDER_REVIEW);
        applicationRepository.save(app);

        ReviewAssignment assignment = ReviewAssignment.builder()
                .application(app)
                .reviewer(reviewer)
                .completed(false)
                .assignedAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(AuditLog.builder()
                .actor(actor)
                .action("REVIEWER_ASSIGNED")
                .entityType("APPLICATION")
                .entityId(app.getId())
                .details("Assigned " + reviewer.getEmail() + " to application " + app.getId())
                .createdAt(LocalDateTime.now())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(reviewer)
                .title("New review assignment")
                .message("You were assigned a new application to review.")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        return assignmentRepository.save(assignment);
    }

    @PostMapping("/applications/{id}/auto-assign")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public ReviewAssignment autoAssign(@PathVariable Long id, HttpServletRequest http) {
        List<User> reviewers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.REVIEWER)
                .toList();

        if (reviewers.isEmpty()) {
            throw new IllegalArgumentException("No reviewers available");
        }

        User reviewer = reviewers.stream()
                .min(Comparator.comparingLong(r -> assignmentRepository.countByReviewerIdAndCompletedFalse(r.getId())))
                .orElseThrow();

        return assignReviewer(id, new ApplicationDtos.AssignReviewerRequest(reviewer.getId()), http);
    }

    @GetMapping("/reviewer/assignments")
    @PreAuthorize("hasRole('REVIEWER')")
    public List<ReviewAssignment> myAssignments(HttpServletRequest http) {
        User reviewer = (User) http.getAttribute("currentUser");
        return assignmentRepository.findByReviewerId(reviewer.getId());
    }

    @PostMapping("/applications/{id}/reviews")
    @PreAuthorize("hasRole('REVIEWER')")
    public Review review(@PathVariable Long id,
                         @RequestBody ApplicationDtos.ReviewRequest request,
                         HttpServletRequest http) {
        User reviewer = (User) http.getAttribute("currentUser");

        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        int total = request.academicScore()
                + request.leadershipScore()
                + request.financialNeedScore()
                + request.communityImpactScore()
                + request.essayScore();

        Review review = Review.builder()
                .application(app)
                .reviewer(reviewer)
                .academicScore(request.academicScore())
                .leadershipScore(request.leadershipScore())
                .financialNeedScore(request.financialNeedScore())
                .communityImpactScore(request.communityImpactScore())
                .essayScore(request.essayScore())
                .totalScore(total)
                .recommendation(request.recommendation())
                .feedback(request.feedback())
                .reviewedAt(LocalDateTime.now())
                .build();

        assignmentRepository.findByReviewerId(reviewer.getId()).stream()
                .filter(a -> a.getApplication().getId().equals(id))
                .forEach(a -> {
                    a.setCompleted(true);
                    assignmentRepository.save(a);
                });

        notificationRepository.save(Notification.builder()
                .recipient(app.getApplicant())
                .title("Application review completed")
                .message("A reviewer has completed feedback for your application.")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        return reviewRepository.save(review);
    }

    @GetMapping("/applications/{id}/reviews")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public List<Review> reviews(@PathVariable Long id) {
        return reviewRepository.findByApplicationId(id);
    }
}
