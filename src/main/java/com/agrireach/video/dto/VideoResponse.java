package com.agrireach.video.dto;

import com.agrireach.video.entity.Video;
import com.agrireach.video.entity.VideoStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class VideoResponse {

    private UUID id;
    private String title;
    private String description;
    private Long fileSize;
    private Long durationSeconds;
    private VideoStatus status;
    private OffsetDateTime createdAt;

    public VideoResponse(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.fileSize = video.getFileSize();
        this.durationSeconds = video.getDurationSeconds();
        this.status = video.getStatus();
        this.createdAt = video.getCreatedAt();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public VideoStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}