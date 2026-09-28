package com.portfolio.linksaver.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.ScrapedVideoInfo;

@Service
public class TiktokVideoService {

    private static final Logger log = LoggerFactory.getLogger(TiktokVideoService.class);

    private final SafeHttpFetcher httpFetcher;

    public TiktokVideoService(SafeHttpFetcher httpFetcher) {
        this.httpFetcher = httpFetcher;
    }

    public ScrapedVideoInfo handleTiktokVideo(NewLink newLink) {
        ScrapedVideoInfo tiktokInfo = new ScrapedVideoInfo();

        String link = newLink.getUrl();

        try {
            if (link.contains("vm.tiktok") || link.contains("vt.tiktok")) {
                link = httpFetcher.resolveFinalUrl(link);
            }
            String oEmbedLink = "https://www.tiktok.com/oembed?url="
                    + URLEncoder.encode(link, StandardCharsets.UTF_8);
            String oEmbedDoc = httpFetcher.fetchBody(oEmbedLink);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(oEmbedDoc);
            if (rootNode.has("title")) {
                tiktokInfo.setTitle(rootNode.get("title").asText());
            }
            if (rootNode.has("thumbnail_url")) {
                tiktokInfo.setThumbnailUrl(rootNode.get("thumbnail_url").asText());
            }
        } catch (IOException e) {
            log.warn("Nie udało się pobrać danych z TikToka dla {}: {}", newLink.getUrl(), e.getMessage());
            throw new RuntimeException("Invalid URL");
        }

        return tiktokInfo;
    }
}
