package com.rentvideo.controller;

import com.rentvideo.entity.Video;
import com.rentvideo.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // Rent a video
    @PostMapping("/{videoId}/rent")
    public ResponseEntity<?> rentVideo(@PathVariable Long videoId, Principal principal) {
        try {
            String message = rentalService.rentVideo(videoId, principal.getName());
            return ResponseEntity.ok(message);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Return a video
    @PostMapping("/{videoId}/return")
    public ResponseEntity<?> returnVideo(@PathVariable Long videoId, Principal principal) {
        try {
            String message = rentalService.returnVideo(videoId, principal.getName());
            return ResponseEntity.ok(message);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // List current user's active rentals
    @GetMapping("/my-rentals")
    public ResponseEntity<List<Video>> getMyRentals(Principal principal) {
        List<Video> videos = rentalService.getUserRentals(principal.getName());
        return ResponseEntity.ok(videos);
    }
}
