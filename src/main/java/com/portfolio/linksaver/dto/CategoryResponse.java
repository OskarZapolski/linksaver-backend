package com.portfolio.linksaver.dto;

import lombok.Getter;

@Getter
public class CategoryResponse {
    private String category;
    private long count;
    private String urlThumbnailImage;

    public CategoryResponse(String category, Long count, String url) {
        this.category = category;
        this.count = count;
        this.urlThumbnailImage = url;
    }
}
