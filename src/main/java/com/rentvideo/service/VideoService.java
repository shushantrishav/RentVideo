package com.rentvideo.service;

import com.rentvideo.dto.VideoRequest;
import com.rentvideo.entity.Video;
import com.rentvideo.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // List only available videos
    public List<Video> getAvailableVideos() {
        return videoRepository.findByAvailableTrue();
    }

    // ADMIN - Add video
    public Video addVideo(VideoRequest request) {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDirector(request.getDirector());
        video.setGenre(request.getGenre());
        video.setAvailable(request.isAvailable());
        return videoRepository.save(video);
    }

    // ADMIN - Update video
    public Video updateVideo(Long id, VideoRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        video.setTitle(request.getTitle());
        video.setDirector(request.getDirector());
        video.setGenre(request.getGenre());
        video.setAvailable(request.isAvailable());

        return videoRepository.save(video);
    }

    // ADMIN - Delete video
    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found");
        }
        videoRepository.deleteById(id);
    }
}
