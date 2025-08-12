package com.rentvideo.app.repository;

import com.rentvideo.app.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
