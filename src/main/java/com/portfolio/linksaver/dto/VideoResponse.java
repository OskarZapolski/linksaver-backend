package com.portfolio.linksaver.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VideoResponse {
    private String category;
    private LocalDateTime saveDate;
    private String urlThumbnailImage;
    private String url;

}
