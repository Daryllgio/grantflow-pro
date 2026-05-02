package com.grantflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Primary
public class LocalStorageService implements StorageService {
    private final Path root;

    public LocalStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
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
