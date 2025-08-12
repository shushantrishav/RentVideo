package com.rentvideo.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "videos", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "title", "director" })
})
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String director;

    @Column(nullable = false)
    private String genre;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true; // Default all videos available as per your requirement

    public Video() {
    }

    public Video(String title, String director, String genre, boolean isAvailable) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
