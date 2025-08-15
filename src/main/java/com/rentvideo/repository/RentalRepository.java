package com.rentvideo.repository;

import com.rentvideo.entity.Rental;
import com.rentvideo.entity.User;
import com.rentvideo.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserAndReturnDateIsNull(User user); // active rentals

    boolean existsByUserAndVideoAndReturnDateIsNull(User user, Video video);

    Rental findByVideoAndReturnDateIsNull(Video video); // currently rented video by anyone
}
