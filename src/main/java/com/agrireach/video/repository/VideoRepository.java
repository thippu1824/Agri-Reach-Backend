package com.agrireach.video.repository;

import com.agrireach.video.entity.Video;
import com.agrireach.video.entity.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    // Fetch all videos for Admin, newest first
    List<Video> findAllByOrderByCreatedAtDesc();

    // Fetch only ACTIVE videos for Employees, newest first
    List<Video> findByStatusOrderByCreatedAtDesc(VideoStatus status);
}