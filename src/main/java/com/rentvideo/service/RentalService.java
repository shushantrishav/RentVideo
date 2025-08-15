package com.rentvideo.service;

import com.rentvideo.entity.Rental;
import com.rentvideo.entity.User;
import com.rentvideo.entity.Video;
import com.rentvideo.repository.RentalRepository;
import com.rentvideo.repository.UserRepository;
import com.rentvideo.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         VideoRepository videoRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    // Rent a video
    public String rentVideo(Long videoId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check user active rentals limit
        List<Rental> activeRentals = rentalRepository.findByUserAndReturnDateIsNull(user);
        if (activeRentals.size() >= 2) {
            throw new RuntimeException("You already have 2 active rentals.");
        }

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        // Check if video is rented by anyone
        Rental activeRental = rentalRepository.findByVideoAndReturnDateIsNull(video);
        if (activeRental != null) {
            throw new RuntimeException("Video '" + video.getTitle() + "' is already rented by "
                    + activeRental.getUser().getEmail());
        }

        // Create rental
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVideo(video);
        rental.setRentalDate(LocalDateTime.now());

        // Update video availability
        video.setAvailable(false);
        videoRepository.save(video);
        rentalRepository.save(rental);

        return "Video '" + video.getTitle() + "' rented successfully";
    }

    // Return a video
    public String returnVideo(Long videoId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Rental rental = rentalRepository.findByUserAndReturnDateIsNull(user).stream()
                .filter(r -> r.getVideo().getId().equals(videoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You have not rented this video."));

        rental.setReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);

        video.setAvailable(true);
        videoRepository.save(video);

        return "Video '" + video.getTitle() + "' returned successfully";
    }

    // List active rentals for a user
    public List<Video> getUserRentals(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Rental> rentals = rentalRepository.findByUserAndReturnDateIsNull(user);

        // Convert rentals to list of videos
        return rentals.stream()
                .map(Rental::getVideo)
                .toList();
    }
}
