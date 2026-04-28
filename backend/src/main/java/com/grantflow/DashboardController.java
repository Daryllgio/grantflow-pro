package com.grantflow;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
    public Map<String, Object> admin() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("users", userRepository.count());
        stats.put("programs", programRepository.count());
        stats.put("applications", applicationRepository.count());
        stats.put("reviews", reviewRepository.count());

        for (ApplicationStatus status : ApplicationStatus.values()) {
            stats.put(status.name().toLowerCase(), applicationRepository.countByStatus(status));
        }

        return stats;
    }
}
