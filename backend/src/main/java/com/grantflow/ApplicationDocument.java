package com.grantflow;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private GrantApplication application;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String fileName;
    private String storageKey;
    private String documentUrl;

    private LocalDateTime uploadedAt;
}
