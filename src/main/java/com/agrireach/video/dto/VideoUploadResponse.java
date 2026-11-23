package com.agrireach.video.dto;

import java.util.UUID;

public class VideoUploadResponse {
    private UUID videoId;
    private String uploadUrl;
    private int expiresInMinutes;
    private String contentType;

    public VideoUploadResponse() {
    }

    public VideoUploadResponse(
        UUID videoId,
        String uploadUrl,
        int expiresInMinutes,
        String contentType
    ) {
        this.videoId = videoId;
        this.uploadUrl = uploadUrl;
        this.expiresInMinutes = expiresInMinutes;
        this.contentType = contentType;
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}