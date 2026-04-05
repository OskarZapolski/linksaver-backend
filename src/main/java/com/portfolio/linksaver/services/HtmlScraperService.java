package com.portfolio.linksaver.services;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.TiktokVideoInfo;
import com.portfolio.linksaver.dto.VideoScrapedData;;

@Service
public class HtmlScraperService {

    private final TiktokVideoService tiktokVideoService;

    public HtmlScraperService(TiktokVideoService tiktokVideoService) {
        this.tiktokVideoService = tiktokVideoService;
    }

    public VideoScrapedData scrapeVideoData(NewLink newLink) {
        VideoScrapedData videoScrapedData = new VideoScrapedData();
        String title ="";
        String imageUrl ="";
        String aiPayload = "";

        if (newLink.getUrl().contains("tiktok.com")) {
            TiktokVideoInfo tiktokVideoInfo = tiktokVideoService.handleTiktokVideo(newLink);
            title = tiktokVideoInfo.getTitle();
            imageUrl = tiktokVideoInfo.getThumbnailUrl();
            aiPayload = title;
        }else {
            Connection connection = setConnection(newLink);
            try {
                Document doc = connection.get();
                imageUrl = getThumbnailImageUrl(doc);
                title = getTitle(doc);
                String description = getDescription(doc);
                String hashtags = getHashtags(doc);
                
                aiPayload = description + " " + hashtags + " " + title;
                
            }catch (IOException e) {
                throw new RuntimeException("Failed to fetch data from the provided URL. The link might be invalid or protected.");
            }
        }
        videoScrapedData.setAiPayload(aiPayload);
        videoScrapedData.setImageUrl(imageUrl);
        return videoScrapedData;
        
    }
    private Connection setConnection(NewLink newLink) {
        Connection connection = Jsoup.connect(newLink.getUrl());
        connection.userAgent("facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)");
        connection.timeout(5000);
        connection.referrer("http://www.google.com");
        return connection;
    }
    
    private String getThumbnailImageUrl(Document doc) {
        Element thumbnailImage = doc.selectFirst("meta[property=og:image]");
        if(thumbnailImage == null){
            thumbnailImage = doc.selectFirst("meta[name=twitter:image]");
        }
        if(thumbnailImage != null){
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
