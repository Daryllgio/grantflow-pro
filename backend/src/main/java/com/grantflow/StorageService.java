package com.grantflow;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    StoredFile store(MultipartFile file, String folder);

    record StoredFile(
            String fileName,
            String storageKey,
            String documentUrl
    ) {}
}
