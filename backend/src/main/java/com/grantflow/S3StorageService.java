package com.grantflow;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "mode", havingValue = "s3")
public class S3StorageService implements StorageService {
    private final S3Client s3Client;
    private final StorageProperties properties;

    public S3StorageService(StorageProperties properties) {
        this.properties = properties;
        this.s3Client = S3Client.builder()
                .region(Region.of(properties.getS3Region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public StoredFile store(MultipartFile file, String folder) {
        try {
            if (properties.getS3Bucket() == null || properties.getS3Bucket().isBlank()) {
                throw new IllegalArgumentException("S3 bucket is not configured");
            }

            String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storageKey = folder + "/" + UUID.randomUUID() + "-" + safeName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(storageKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            String url = "https://" + properties.getS3Bucket() + ".s3." + properties.getS3Region()
                    + ".amazonaws.com/" + storageKey;

            return new StoredFile(safeName, storageKey, url);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not upload file to S3: " + e.getMessage());
        }
    }
}
