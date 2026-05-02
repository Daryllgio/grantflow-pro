package com.grantflow;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "mode", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    private final Path root;

    public LocalStorageService(StorageProperties properties) {
        this.root = Paths.get(properties.getLocalUploadDir()).toAbsolutePath().normalize();
    }

    @Override
    public StoredFile store(MultipartFile file, String folder) {
        try {
            Files.createDirectories(root.resolve(folder));

            String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storageKey = folder + "/" + UUID.randomUUID() + "-" + safeName;

            Path destination = root.resolve(storageKey).normalize();
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return new StoredFile(
                    safeName,
                    storageKey,
                    "/uploads/" + storageKey
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not store file: " + e.getMessage());
        }
    }
}
