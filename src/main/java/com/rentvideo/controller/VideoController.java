package com.rentvideo.controller;

import com.rentvideo.dto.VideoRequest;
import com.rentvideo.entity.Video;
import com.rentvideo.service.RentalService;
import com.rentvideo.service.VideoService;
import com.rentvideo.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;
    private final RentalService rentalService;
    private final JwtUtil jwtUtil;

    public VideoController(VideoService videoService, RentalService rentalService, JwtUtil jwtUtil) {
        this.videoService = videoService;
        this.rentalService = rentalService;
        this.jwtUtil = jwtUtil;
    }

    // View all available videos
    @GetMapping
    public ResponseEntity<List<Video>> getAllAvailableVideos() {
        return ResponseEntity.ok(videoService.getAvailableVideos());
    }

    // ADMIN - Add new video
    @PostMapping
    public ResponseEntity<Video> addVideo(@RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.addVideo(request));
    }

    // ADMIN - Update video
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.updateVideo(id, request));
    }

    // ADMIN - Delete video
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted successfully");
    }

    // USER - Rent a video
    @PostMapping("/rent/{id}")
    public ResponseEntity<String> rentVideo(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String userEmail = jwtUtil.extractUsername(token);

        String message = rentalService.rentVideo(id, userEmail);
        return ResponseEntity.ok(message);
    }

    // USER - Return a video
    @PostMapping("/return/{id}")
    public ResponseEntity<String> returnVideo(@PathVariable Long id,
                                              @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String userEmail = jwtUtil.extractUsername(token);

        String message = rentalService.returnVideo(id, userEmail);
        return ResponseEntity.ok(message);
    }

}
