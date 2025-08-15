package com.rentvideo.dto;

import lombok.Data;

@Data
public class VideoRequest {
    private String title;
    private String director;
    private String genre;
    private boolean available; // true if available for rent
}
