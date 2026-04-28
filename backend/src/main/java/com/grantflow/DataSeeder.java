package com.grantflow;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .fullName("GrantFlow Admin")
                    .email("admin@grantflow.dev")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build());

            userRepository.save(User.builder()
                    .fullName("Sarah Reviewer")
                    .email("reviewer@grantflow.dev")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.REVIEWER)
                    .createdAt(LocalDateTime.now())
                    .build());

            userRepository.save(User.builder()
                    .fullName("Alex Applicant")
                    .email("applicant@grantflow.dev")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.APPLICANT)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        if (programRepository.count() == 0) {
            programRepository.save(Program.builder()
                    .name("Gwags Scholars Program 2026")
                    .description("Scholarship program supporting high-potential undergraduate students.")
                    .awardAmount(new BigDecimal("150000.00"))
                    .applicationDeadline(LocalDate.of(2026, 5, 31))
                    .eligibilityCriteria("Applicants must be undergraduate students with demonstrated leadership, academic promise, and financial need.")
                    .status(ProgramStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            programRepository.save(Program.builder()
                    .name("Community Leadership Microgrant")
                    .description("Small grant for student-led community impact projects.")
                    .awardAmount(new BigDecimal("500.00"))
                    .applicationDeadline(LocalDate.of(2026, 8, 15))
                    .eligibilityCriteria("Applicants must propose a community project with a measurable impact plan.")
                    .status(ProgramStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
    }
}
