package com.grantflow;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    private String mode = "local";
    private String localUploadDir = "uploads";
    private String s3Bucket;
    private String s3Region = "us-east-1";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLocalUploadDir() {
        return localUploadDir;
    }

    public void setLocalUploadDir(String localUploadDir) {
        this.localUploadDir = localUploadDir;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3Region() {
        return s3Region;
    }

    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }
}
