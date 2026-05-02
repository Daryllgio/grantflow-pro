package com.grantflow;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {
    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final StorageService storageService;

    @PostMapping(value = "/applications/{id}/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationDocument uploadDocument(@PathVariable Long id,
                                              @RequestParam DocumentType documentType,
                                              @RequestParam("file") MultipartFile file) {
        GrantApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        StorageService.StoredFile stored = storageService.store(file, "applications/" + id);

        ApplicationDocument doc = ApplicationDocument.builder()
                .application(app)
                .documentType(documentType)
                .fileName(stored.fileName())
                .storageKey(stored.storageKey())
                .documentUrl(stored.documentUrl())
                .uploadedAt(LocalDateTime.now())
                .build();

        return documentRepository.save(doc);
    }

    @GetMapping("/applications/{id}/documents/list")
    public List<ApplicationDocument> listDocuments(@PathVariable Long id) {
        return documentRepository.findByApplicationId(id);
    }
}
