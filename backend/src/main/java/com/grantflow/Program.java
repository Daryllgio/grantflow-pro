package com.grantflow;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal awardAmount;
    private LocalDate applicationDeadline;

    @Column(length = 3000)
    private String eligibilityCriteria;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
