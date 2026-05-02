package com.grantflow;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        resetDemoData();
        seedUsers();
        seedPrograms();
        seedApplications();
    }

    private void resetDemoData() {
        reviewRepository.deleteAll();
        applicationDocumentRepository.deleteAll();
        notificationRepository.deleteAll();
        auditLogRepository.deleteAll();
        applicationRepository.deleteAll();
        programRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .fullName("GrantFlow Admin")
                .email("admin@grantflow.dev")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build());

        userRepository.save(User.builder()
                .fullName("Alex Applicant")
                .email("applicant@grantflow.dev")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.APPLICANT)
                .createdAt(LocalDateTime.now())
                .build());

        for (int i = 1; i <= 20; i++) {
            userRepository.save(User.builder()
                    .fullName("Demo Applicant " + i)
                    .email("demoapplicant" + i + "@grantflow.dev")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.APPLICANT)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }

    private void seedPrograms() {
        List<Program> programs = List.of(
                program("Global Health Equity Scholars Fund", "Supports students pursuing medicine, nursing, public health, pharmacy, or biomedical sciences with a demonstrated commitment to improving health access in underserved communities.", "5000.00", LocalDate.of(2026, 6, 30)),
                program("Community Innovation Microgrant", "Funds student-led projects that solve local problems through practical innovation, volunteer coordination, education, or community service.", "750.00", LocalDate.of(2026, 7, 15)),
                program("First-Generation Excellence Scholarship", "Provides financial support to first-generation college students who have shown academic resilience, leadership, and a clear plan for long-term professional growth.", "3000.00", LocalDate.of(2026, 8, 1)),
                program("Women in Technology Advancement Award", "Supports women pursuing careers in software engineering, cybersecurity, data science, product management, artificial intelligence, and emerging technology fields.", "4500.00", LocalDate.of(2026, 9, 10)),
                program("Rural Education Access Grant", "Designed for students from rural communities who plan to use their education to expand access to healthcare, education, infrastructure, or economic opportunity.", "2500.00", LocalDate.of(2026, 7, 22)),
                program("Youth Entrepreneurship Launch Fund", "Provides early funding for young founders building ventures, nonprofits, social enterprises, or community programs with a clear launch plan and measurable impact.", "6000.00", LocalDate.of(2026, 10, 5)),
                program("Public Service Leadership Scholarship", "Recognizes students committed to civic engagement, government service, nonprofit leadership, legal advocacy, or community organizing.", "3500.00", LocalDate.of(2026, 8, 18)),
                program("Digital Equity Innovation Fund", "Supports projects that improve access to technology, digital literacy, online learning, or software tools for underserved populations.", "7000.00", LocalDate.of(2026, 11, 1)),
                program("Environmental Justice Action Grant", "Funds students and teams working on climate resilience, air quality, water access, sustainability education, or environmental health advocacy.", "4200.00", LocalDate.of(2026, 9, 25)),
                program("Creative Impact Scholarship", "Supports students using design, media, writing, fashion, film, music, or visual storytelling to create cultural, educational, or social impact.", "2000.00", LocalDate.of(2026, 7, 30)),
                program("Transfer Student Success Award", "Provides support to transfer students who have shown persistence, academic improvement, leadership, and strong commitment to completing their degree.", "2800.00", LocalDate.of(2026, 8, 12)),
                program("Black STEM Scholars Fellowship", "Supports Black students in STEM fields who demonstrate academic strength, leadership, mentorship, research potential, or service to their communities.", "5500.00", LocalDate.of(2026, 9, 3)),
                program("Mental Health Advocacy Grant", "Funds students or teams building programs around mental health awareness, peer support, wellness education, or community-based intervention.", "3200.00", LocalDate.of(2026, 10, 14)),
                program("Global Citizenship Scholarship", "Supports students with international experience, cross-cultural leadership, diplomacy interests, refugee advocacy, or global development goals.", "3800.00", LocalDate.of(2026, 11, 20)),
                program("Community Health Outreach Fund", "Supports medical, pharmacy, dental, nursing, and public health students organizing screenings, health education, prevention campaigns, or outreach events.", "6500.00", LocalDate.of(2026, 8, 8)),
                program("AI for Social Good Grant", "Funds student projects using artificial intelligence, data science, or automation to address education, health, civic, environmental, or nonprofit challenges.", "8000.00", LocalDate.of(2026, 12, 5)),
                program("Immigrant Student Opportunity Award", "Provides financial support to immigrant, refugee, and internationally educated students navigating academic, financial, and career barriers.", "2700.00", LocalDate.of(2026, 7, 5)),
                program("Research Potential Fellowship", "Supports undergraduate students preparing for graduate school, medical school, MD/PhD programs, or research-intensive careers.", "4800.00", LocalDate.of(2026, 9, 17)),
                program("Social Justice Leadership Fund", "Recognizes students advancing equity through advocacy, education, law, policy, organizing, or direct community support.", "3600.00", LocalDate.of(2026, 10, 30)),
                program("Small Business Builder Grant", "Supports students launching small businesses, community ventures, creative brands, or service-based businesses with realistic execution plans.", "5000.00", LocalDate.of(2026, 11, 15)),
                program("Education Mentorship Scholarship", "Supports students creating mentorship, tutoring, college-access, career-readiness, or literacy programs for younger students.", "2200.00", LocalDate.of(2026, 6, 28)),
                program("Civic Technology Fellowship", "Funds students building technology that improves public services, government transparency, nonprofit operations, or community participation.", "6200.00", LocalDate.of(2026, 12, 10)),
                program("Healthcare Access Innovation Award", "Supports ideas that reduce barriers to care, improve patient navigation, expand outreach, or strengthen community-based health systems.", "7200.00", LocalDate.of(2026, 9, 28)),
                program("Student Athlete Leadership Award", "Recognizes student athletes who demonstrate leadership, discipline, service, academic commitment, and positive influence on peers.", "2500.00", LocalDate.of(2026, 7, 19)),
                program("Arts and Culture Preservation Fund", "Supports students preserving cultural heritage, language, storytelling, traditional arts, community history, or creative education.", "3000.00", LocalDate.of(2026, 8, 27)),
                program("Data for Community Impact Grant", "Funds projects using data analysis, mapping, dashboards, surveys, or visualization to support local decision-making and social impact.", "5600.00", LocalDate.of(2026, 10, 8)),
                program("Disability Access Scholarship", "Supports students advocating for accessibility, inclusive design, disability justice, assistive technology, or accessible education.", "4100.00", LocalDate.of(2026, 9, 9)),
                program("Youth Policy and Governance Award", "Supports students interested in public policy, governance, youth participation, diplomacy, civic leadership, or institutional reform.", "3900.00", LocalDate.of(2026, 11, 2)),
                program("Food Security Action Grant", "Funds student-led projects addressing hunger, nutrition education, food distribution, school meals, or sustainable agriculture.", "3300.00", LocalDate.of(2026, 8, 3)),
                program("STEM Transfer Pathways Scholarship", "Supports transfer students entering STEM fields who demonstrate academic growth, persistence, and commitment to research or innovation.", "4400.00", LocalDate.of(2026, 7, 29)),
                program("Community Safety Innovation Fund", "Supports youth-led projects focused on violence prevention, emergency preparedness, neighborhood safety, or community trust-building.", "3700.00", LocalDate.of(2026, 10, 22)),
                program("Language Access and Literacy Grant", "Funds projects improving multilingual access, literacy, translation, communication equity, or educational support for language learners.", "2600.00", LocalDate.of(2026, 9, 14)),
                program("Future Physicians Access Scholarship", "Supports pre-medical students from underserved backgrounds who demonstrate clinical service, research interest, and commitment to patient care.", "6000.00", LocalDate.of(2026, 8, 31)),
                program("Technology Product Leadership Award", "Supports students pursuing product management, technical program management, business analysis, or technology strategy roles.", "5200.00", LocalDate.of(2026, 12, 18)),
                program("Nonprofit Operations Capacity Grant", "Funds student or youth-led nonprofit teams improving operations, fundraising, volunteer management, program delivery, or impact measurement.", "4700.00", LocalDate.of(2026, 11, 26))
        );

        programRepository.saveAll(programs);
    }

    private Program program(String name, String description, String amount, LocalDate deadline) {
        return Program.builder()
                .name(name)
                .description(description)
                .awardAmount(new BigDecimal(amount))
                .applicationDeadline(deadline)
                .eligibilityCriteria("Applicants must demonstrate alignment with the program mission, a clear need for support, and a credible plan for academic, professional, or community impact.")
                .status(ProgramStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void seedApplications() {
        User admin = userRepository.findByEmail("admin@grantflow.dev").orElseThrow();
        User alex = userRepository.findByEmail("applicant@grantflow.dev").orElseThrow();

        List<Program> programs = programRepository.findAll();

        ApplicationStatus[] statusMix = {
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.APPROVED,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.REJECTED,
                ApplicationStatus.REJECTED,
                ApplicationStatus.REJECTED,
                ApplicationStatus.REJECTED,
                ApplicationStatus.WAITLISTED,
                ApplicationStatus.WAITLISTED,
                ApplicationStatus.WAITLISTED
        };

        for (int i = 0; i < 25; i++) {
            User applicant = alex;
            Program program = programs.get(i % programs.size());
            ApplicationStatus status = statusMix[i];

            GrantApplication app = GrantApplication.builder()
                    .applicant(applicant)
                    .program(program)
                    .personalStatement(responseOne(program.getName()))
                    .academicBackground(responseTwo(program.getName()))
                    .financialNeedStatement(responseThree(program.getName()))
                    .leadershipExperience("")
                    .communityImpact("")
                    .status(status)
                    .riskFlag(false)
                    .submittedAt(LocalDateTime.now().minusDays(20 - (i % 12)))
                    .createdAt(LocalDateTime.now().minusDays(30 - (i % 15)))
                    .updatedAt(LocalDateTime.now().minusDays(i % 8))
                    .build();

            applicationRepository.save(app);

            if (status == ApplicationStatus.APPROVED || status == ApplicationStatus.REJECTED || status == ApplicationStatus.WAITLISTED) {
                reviewRepository.save(Review.builder()
                        .application(app)
                        .reviewer(admin)
                        .academicScore(15 + (i % 6))
                        .leadershipScore(14 + (i % 7))
                        .financialNeedScore(13 + (i % 8))
                        .communityImpactScore(15 + (i % 5))
                        .essayScore(14 + (i % 6))
                        .totalScore(71 + (i % 25))
                        .recommendation(status == ApplicationStatus.APPROVED ? Recommendation.APPROVE :
                                status == ApplicationStatus.REJECTED ? Recommendation.REJECT : Recommendation.HOLD)
                        .feedback("Reviewed for mission alignment, clarity of responses, demonstrated need, and expected community impact.")
                        .reviewedAt(LocalDateTime.now().minusDays(i % 7))
                        .build());
            }

            notificationRepository.save(Notification.builder()
                    .recipient(alex)
                    .title(program.getName() + " update")
                    .message(notificationMessage(program.getName(), status))
                    .read(true)
                    .createdAt(LocalDateTime.now().minusDays(i % 6))
                    .build());

            auditLogRepository.save(AuditLog.builder()
                    .actor(admin)
                    .action(status == ApplicationStatus.SUBMITTED ? "APPLICATION_SUBMITTED" : "APPLICATION_STATUS_UPDATED")
                    .entityType("APPLICATION")
                    .entityId(app.getId())
                    .details(program.getName() + " application is currently " + status)
                    .createdAt(LocalDateTime.now().minusDays(i % 10))
                    .build());
        }
    }

    private String responseOne(String programName) {
        return "I am applying to the " + programName + " because it directly aligns with my academic goals, leadership development, and long-term commitment to serving communities that face limited access to opportunity.";
    }

    private String responseTwo(String programName) {
        return "My experiences include academic persistence, leadership in student or community initiatives, and consistent responsibility in environments where others depended on my ability to organize, communicate, and follow through.";
    }

    private String responseThree(String programName) {
        return "If selected, I would use the support to reduce financial barriers, continue my education, strengthen my professional development, and invest more time in work that creates measurable community value.";
    }

    private String notificationMessage(String programName, ApplicationStatus status) {
        return switch (status) {
            case SUBMITTED -> "Your application for " + programName + " was submitted successfully.";
            case UNDER_REVIEW -> "Your application for " + programName + " is now under review.";
            case APPROVED -> "Congratulations. Your application for " + programName + " was approved.";
            case REJECTED -> "Your application for " + programName + " was reviewed and was not selected.";
            case WAITLISTED -> "Your application for " + programName + " has been placed on the waitlist.";
            default -> "Your application for " + programName + " was updated.";
        };
    }
}
