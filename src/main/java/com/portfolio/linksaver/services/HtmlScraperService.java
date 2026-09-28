package com.portfolio.linksaver.services;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.ScrapedVideoInfo;
import com.portfolio.linksaver.dto.VideoScrapedData;;

@Service
public class HtmlScraperService {

    private static final Logger log = LoggerFactory.getLogger(HtmlScraperService.class);

    private final TiktokVideoService tiktokVideoService;
    private final InstagramVideoService instagramVideoService;
    private final SafeHttpFetcher httpFetcher;

    public HtmlScraperService(TiktokVideoService tiktokVideoService, InstagramVideoService instagramVideoService,
            SafeHttpFetcher httpFetcher) {
        this.tiktokVideoService = tiktokVideoService;
        this.instagramVideoService = instagramVideoService;
        this.httpFetcher = httpFetcher;
    }

    public VideoScrapedData scrapeVideoData(NewLink newLink) {
        VideoScrapedData videoScrapedData = new VideoScrapedData();
        String title = "";
        String imageUrl = "";
        String aiPayload = "";

        if (newLink.getUrl().contains("tiktok")) {
            ScrapedVideoInfo tiktokVideoInfo = tiktokVideoService.handleTiktokVideo(newLink);
            title = tiktokVideoInfo.getTitle();
            imageUrl = tiktokVideoInfo.getThumbnailUrl();
            aiPayload = title;
        } else if (newLink.getUrl().contains("instagram")) {
            ScrapedVideoInfo scrapedVideoInfo = instagramVideoService.handleInstagramVideo(newLink);
            title = scrapedVideoInfo.getTitle();
            imageUrl = scrapedVideoInfo.getThumbnailUrl();
            aiPayload = title;
        } else {
            try {
                Document doc = httpFetcher.fetchDocument(newLink.getUrl());
                imageUrl = getThumbnailImageUrl(doc);
                title = getTitle(doc);
                String hashtags = getHashtags(doc);
                String description = getDescription(doc);

                aiPayload = "Tytuł: " + (title != null ? title : "Brak tytułu") +
                        ", Opis: " + (description != null ? description : "Brak opisu") +
                        ", Tagi: " + (hashtags != null ? hashtags : "Brak tagów");

            } catch (IOException e) {
                log.warn("Nie udało się pobrać strony {}: {}", newLink.getUrl(), e.getMessage());
                throw new RuntimeException("Invalid URL");
            }
        }
        videoScrapedData.setAiPayload(aiPayload);
        videoScrapedData.setImageUrl(imageUrl);
        return videoScrapedData;

    }

    private String getThumbnailImageUrl(Document doc) {
        Element thumbnailImage = doc.selectFirst("meta[property=og:image]");
        if (thumbnailImage == null) {
            thumbnailImage = doc.selectFirst("meta[name=twitter:image]");
        }
        if (thumbnailImage != null) {
            String imageUrl = thumbnailImage.attr("content");
            return imageUrl;
        }
        return null;
    }

    private String getTitle(Document doc) {
        Element title = doc.selectFirst("meta[property=og:title]");
        if (title == null) {
            title = doc.selectFirst("meta[name=twitter:title]");
        }
        if (title != null) {
            return title.attr("content");
        }
        return null;
    }

    private String getDescription(Document doc) {
        Element description = doc.selectFirst("meta[property=og:description]");
        if (description == null) {
            description = doc.selectFirst("meta[name=twitter:description]");
        }
        if (description != null) {
            return description.attr("content");
        }
        return null;
    }

    private String getHashtags(Document doc) {
        Element hashtags = doc.selectFirst("meta[name=keywords]");
        if (hashtags != null) {
            return hashtags.attr("content");
        }
        return null;
    }
}
