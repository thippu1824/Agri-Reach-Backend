package com.agrireach.storage;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public interface StorageProvider {

    String generatePresignedUploadUrl(
        String objectKey,
        String contentType,
        int expirationMinutes
    );

    String generatePresignedDownloadUrl(
        String objectKey,
        int expirationMinutes
    );

    void delete(String objectKey);

    StorageObjectMetadata getObjectMetadata(String objectKey);
}