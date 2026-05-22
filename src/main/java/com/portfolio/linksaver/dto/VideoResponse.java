package com.portfolio.linksaver.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoResponse {
    private Long videoId;
    private String category;
    private LocalDateTime saveDate;
    private String urlThumbnailImage;
    private String url;
}
