package com.rentvideo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String director;
    private String genre;

    @Column(nullable = false)
    private boolean available = true; // default available

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Rental> rentals;

    @ManyToOne
    @JoinColumn(name = "rented_by_id")
    private User rentedBy; 
}
