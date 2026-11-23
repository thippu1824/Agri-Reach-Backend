package com.agrireach.video.service;

import com.agrireach.storage.StorageObjectMetadata;
import com.agrireach.storage.StorageProvider;
import com.agrireach.video.dto.VideoUploadRequest;
import com.agrireach.video.dto.VideoUploadResponse;
import com.agrireach.video.entity.Video;
import com.agrireach.video.entity.VideoStatus;
import com.agrireach.video.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
public class VideoService {
    private static final Logger logger =
        LoggerFactory.getLogger(VideoService.class);
    private static final int UPLOAD_URL_EXPIRATION_MINUTES = 15;
    private final VideoRepository videoRepository;
    private final StorageProvider storageProvider;
    @Value("${video.upload.max-size-bytes:524288000}")
    private long maxVideoSizeBytes;

    public VideoService(VideoRepository videoRepository, StorageProvider storageProvider) {
        this.videoRepository = videoRepository;
        this.storageProvider = storageProvider;
        logger.info(
            "VideoService initialized. repository={}, storageProvider={}",
            videoRepository.getClass().getSimpleName(),
            storageProvider.getClass().getSimpleName()
        );
    }

    @Transactional
    public VideoUploadResponse createUploadRequest(VideoUploadRequest request) {
        logger.info(
            "Creating video upload request. title={}, contentType={}, fileSize={}",
            request.getTitle(),
            request.getContentType(),
            request.getFileSize()
        );
        // Validate request
        if (request.getTitle() == null ||
            request.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                "Video title is required"
            );
        }
        if (!"video/mp4".equalsIgnoreCase(request.getContentType())) {
            throw new IllegalArgumentException(
                "Only MP4 videos are currently supported"
            );
        }
        if (request.getFileSize() == null ||
            request.getFileSize() <= 0 ||
            request.getFileSize() > maxVideoSizeBytes) {
            throw new IllegalArgumentException(
                "Invalid video file size"
            );
        }
        // Generate unique video ID and R2 object key
        String objectKey = "videos/" + UUID.randomUUID() + ".mp4";

        // Create video entity
        Video video = new Video();
        video.setTitle(request.getTitle().trim());
        video.setDescription(request.getDescription());
        video.setObjectKey(objectKey);
        video.setContentType(request.getContentType());
        video.setFileSize(request.getFileSize());
        video.setStatus(VideoStatus.PENDING);

        // Save pending record in PostgreSQL
        Video savedVideo = videoRepository.save(video);

        UUID videoId = savedVideo.getId();
        logger.info(
            "Pending video record created. videoId={}, objectKey={}",
            videoId,
            objectKey
        );

        // Generate presigned R2 upload URL
        String uploadUrl = storageProvider.generatePresignedUploadUrl(
            objectKey,
            request.getContentType(),
            UPLOAD_URL_EXPIRATION_MINUTES
        );

        logger.info(
            "Video upload URL generated successfully. videoId={}, expiresInMinutes={}",
            videoId,
            UPLOAD_URL_EXPIRATION_MINUTES
        );

        return new VideoUploadResponse(
            videoId,
            uploadUrl,
            UPLOAD_URL_EXPIRATION_MINUTES,
            request.getContentType()
        );
    }

    @Transactional
    public void confirmUpload(UUID videoId) {
        logger.info("Confirming video upload. videoId={}", videoId);

        // Find the video record
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Video not found"
            ));
        // Only pending videos can be confirmed
        if (video.getStatus() != VideoStatus.PENDING) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Video is not in PENDING status"
            );
        }
        // Check whether the uploaded object exists in R2
        StorageObjectMetadata metadata;
        try {
            metadata = storageProvider.getObjectMetadata(video.getObjectKey());
        } catch (RuntimeException exception) {
            logger.error(
                "Unable to verify uploaded video in storage. videoId={}, objectKey={}",
                videoId,
                video.getObjectKey(),
                exception
            );
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Uploaded video could not be verified in storage"
            );
        }
        // Verify actual uploaded file size
        if (metadata.getContentLength() != video.getFileSize()) {
            logger.warn(
                "Video upload size mismatch. videoId={}, expectedSize={}, actualSize={}",
                videoId,
                video.getFileSize(),
                metadata.getContentLength()
            );
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Uploaded video size does not match the requested file size"
            );
        }
        // Verify actual uploaded content type
        String actualContentType = metadata.getContentType() == null
            ? ""
            : metadata.getContentType().toLowerCase(Locale.ROOT);
        String expectedContentType = video.getContentType()
            .toLowerCase(Locale.ROOT);
        if (!expectedContentType.equals(actualContentType)) {
            logger.warn(
                "Video upload content type mismatch. videoId={}, expectedType={}, actualType={}",
                videoId,
                expectedContentType,
                actualContentType
            );
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Uploaded video content type does not match"
            );
        }
        // Activate the video after successful verification
        video.setStatus(VideoStatus.ACTIVE);
        videoRepository.save(video);
        logger.info(
            "Video upload confirmed successfully. videoId={}, size={} bytes",
            videoId,
            metadata.getContentLength()
        );
    }
}