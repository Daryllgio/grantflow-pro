package com.grantflow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewAssignmentRepository extends JpaRepository<ReviewAssignment, Long> {
    List<ReviewAssignment> findByReviewerId(Long reviewerId);
    long countByReviewerIdAndCompletedFalse(Long reviewerId);
}
