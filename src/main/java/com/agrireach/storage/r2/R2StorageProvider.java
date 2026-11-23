package com.agrireach.storage.r2;

import java.time.Duration;

import com.agrireach.storage.StorageObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.agrireach.storage.StorageProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@ConditionalOnProperty(
    name = "storage.provider",
    havingValue = "r2"
)
public class R2StorageProvider implements StorageProvider {

    private static final Logger logger =
        LoggerFactory.getLogger(R2StorageProvider.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public R2StorageProvider(S3Client s3Client, S3Presigner s3Presigner,
        @Value("${cloudflare.r2.bucket-name}") String bucketName) {
        logger.info("Entering R2StorageProvider constructor");
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        logger.info("R2StorageProvider initialized successfully");
        logger.info("Exiting R2StorageProvider constructor");
    }

    @Override
    public String generatePresignedUploadUrl(String objectKey, String contentType, int expirationMinutes) {
        logger.info("Entering generatePresignedUploadUrl()");
        try {
            validateExpiration(expirationMinutes);
            PutObjectRequest putRequest =
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                    .signatureDuration(
                        Duration.ofMinutes(expirationMinutes))
                    .putObjectRequest(putRequest)
                    .build();
            String url = s3Presigner
                .presignPutObject(presignRequest)
                .url()
                .toString();
            logger.info(
                "Upload URL generated successfully for object: {}",
                objectKey);
            logger.info("Exiting generatePresignedUploadUrl()");
            return url;
        } catch (RuntimeException exception) {
            logger.error(
                "Error in generatePresignedUploadUrl() for object: {}",
                objectKey,
                exception);
            throw exception;
        }
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey, int expirationMinutes) {

        logger.info("Entering generatePresignedDownloadUrl()");
        try {
            validateExpiration(expirationMinutes);
            GetObjectRequest getRequest =
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                    .signatureDuration(
                        Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(getRequest)
                    .build();
            String url = s3Presigner
                .presignGetObject(presignRequest)
                .url()
                .toString();
            logger.info(
                "Playback URL generated successfully for object: {}",
                objectKey);
            logger.info("Exiting generatePresignedDownloadUrl()");
            return url;
        } catch (RuntimeException exception) {
            logger.error(
                "Error in generatePresignedDownloadUrl() for object: {}",
                objectKey,
                exception);
            throw exception;
        }
    }

    @Override
    public void delete(String objectKey) {
        logger.info("Entering delete()");
        try {
            DeleteObjectRequest deleteRequest =
                DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteRequest);
            logger.info(
                "Video object deleted successfully: {}",
                objectKey);
            logger.info("Exiting delete()");
        } catch (RuntimeException exception) {
            logger.error(
                "Error in delete() for object: {}",
                objectKey,
                exception);
            throw exception;
        }
    }

    @Override
    public StorageObjectMetadata getObjectMetadata(String objectKey) {
        logger.info("Checking storage metadata for object: {}", objectKey);

        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
            HeadObjectResponse response = s3Client.headObject(request);
            StorageObjectMetadata metadata = new StorageObjectMetadata(
                response.contentLength(),
                response.contentType()
            );
            logger.info(
                "Storage metadata retrieved successfully for object: {}, size: {} bytes, content type: {}",
                objectKey,
                metadata.getContentLength(),
                metadata.getContentType()
            );
            return metadata;
        } catch (RuntimeException exception) {
            logger.error(
                "Failed to retrieve storage metadata for object: {}",
                objectKey,
                exception
            );
            throw exception;
        }
    }

    private void validateExpiration(int expirationMinutes) {
        logger.info("Entering validateExpiration()");

        try {
            if (expirationMinutes <= 0 || expirationMinutes > 10080) {
                throw new IllegalArgumentException(
                    "Presigned URL expiration must be between 1 and 10080 minutes");
            }
            logger.info("Exiting validateExpiration()");
        } catch (RuntimeException exception) {
            logger.error(
                "Error in validateExpiration(): {}",
                exception.getMessage(),
                exception);
            throw exception;
        }
    }


    public void checkConnection() {
        logger.info("Entering checkConnection()");

        try {
            s3Client.headBucket(
                HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build()
            );
            logger.info("Cloudflare R2 bucket connection successful");
            logger.info("Exiting checkConnection()");
        } catch (RuntimeException exception) {
            logger.error(
                "Cloudflare R2 bucket connection failed",
                exception
            );
            throw exception;
        }
    }


}