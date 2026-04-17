package com.portfolio.linksaver.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class CategoryResponse {
    private String category;
    private long count;
    private String urlThumbnailImage;
    private LocalDateTime saveDate;

    public CategoryResponse(String category, Long count, String url, LocalDateTime saveDate) {
        this.category = category;
        this.count = count;
        this.urlThumbnailImage = url;
        this.saveDate = saveDate;
    }
}
