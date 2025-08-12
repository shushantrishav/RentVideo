package com.rentvideo.app.service;

import com.rentvideo.app.exception.ResourceNotFoundException;
import com.rentvideo.app.model.Video;
import com.rentvideo.app.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // Get all videos
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    // Get video by id
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }

    // Create a new video
    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    // Update existing video
    public Video updateVideo(Long id, Video updatedVideo) {
        Video video = getVideoById(id);
        video.setTitle(updatedVideo.getTitle());
        video.setDirector(updatedVideo.getDirector());
        video.setGenre(updatedVideo.getGenre());
        video.setAvailable(updatedVideo.isAvailable());  // Use isAvailable() getter, setAvailable() setter
        return videoRepository.save(video);
    }

    // Delete a video
    public void deleteVideo(Long id) {
        Video video = getVideoById(id);
        videoRepository.delete(video);
    }
}
