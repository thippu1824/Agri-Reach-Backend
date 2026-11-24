package com.agrireach.video.controller;

import com.agrireach.video.dto.VideoResponse;
import com.agrireach.video.dto.VideoUploadRequest;
import com.agrireach.video.dto.VideoUploadResponse;
import com.agrireach.video.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private static final Logger logger =
        LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
        logger.info("VideoController initialized. service={}",
            videoService.getClass().getSimpleName());
    }

    @PostMapping("/upload-request")
    public ResponseEntity<VideoUploadResponse> createUploadRequest(@RequestBody VideoUploadRequest request) {
        logger.info("Received video upload request. title={}, contentType={}, fileSize={}",
            request.getTitle(),
            request.getContentType(),
            request.getFileSize());

        VideoUploadResponse response =
            videoService.createUploadRequest(request);

        logger.info("Video upload request processed successfully. videoId={}", response.getVideoId());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PostMapping("/{videoId}/confirm-upload")
    public ResponseEntity<Void> confirmUpload(@PathVariable UUID videoId) {
        logger.info("Received video upload confirmation request. videoId={}", videoId);

        videoService.confirmUpload(videoId);
        logger.info("Video upload confirmation completed. videoId={}", videoId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<VideoResponse>> getAllVideosForAdmin() {
        logger.info("Received request to fetch all videos for Admin");

        List<VideoResponse> videos = videoService.getAllVideos();

        logger.info(
            "Returning video list to Admin. count={}",
            videos.size()
        );
        return ResponseEntity.ok(videos);
    }
    @GetMapping("/employee")
    public ResponseEntity<List<VideoResponse>> getActiveVideosForEmployee() {
        logger.info("Received request to fetch active videos for Employee");

        List<VideoResponse> videos = videoService.getActiveVideos();

        logger.info(
            "Returning active video list to Employee. count={}",
            videos.size()
        );
        return ResponseEntity.ok(videos);
    }
}