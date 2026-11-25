
package com.agrireach.video.dto;

import java.util.UUID;

public class VideoDownloadResponse {

    private UUID videoId;
    private String downloadUrl;
    private int expiresInMinutes;
    private Long fileSize;
    private String contentType;
    private String title;

    public VideoDownloadResponse(
            UUID videoId,
            String downloadUrl,
            int expiresInMinutes,
            Long fileSize,
            String contentType,
            String title) {

        this.videoId = videoId;
        this.downloadUrl = downloadUrl;
        this.expiresInMinutes = expiresInMinutes;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.title = title;
    }

    public UUID getVideoId() {
        return videoId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }
}
