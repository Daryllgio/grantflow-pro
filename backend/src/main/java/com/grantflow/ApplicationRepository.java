package com.grantflow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<GrantApplication, Long> {
    List<GrantApplication> findByApplicantId(Long applicantId);
    long countByStatus(ApplicationStatus status);
}
