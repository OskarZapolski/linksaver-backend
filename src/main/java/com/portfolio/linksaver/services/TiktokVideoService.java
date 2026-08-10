package com.portfolio.linksaver.services;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Connection;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.ScrapedVideoInfo;

@Service
public class TiktokVideoService {

    public ScrapedVideoInfo handleTiktokVideo(NewLink newLink) {
        ScrapedVideoInfo tiktokInfo = new ScrapedVideoInfo();

        String link = newLink.getUrl();

        try {
            if (link.contains("vm.tiktok") || link.contains("vt.tiktok")) {
                Connection.Response resp = Jsoup.connect(link)
                        .followRedirects(true)
                        .execute();
                link = resp.url().toString();

            }
            String oEmbedLink = "https://www.tiktok.com/oembed?url=" + link;
            Connection oEmbedConnection = Jsoup.connect(oEmbedLink).ignoreContentType(true);
            String oEmbedDoc = oEmbedConnection.timeout(5000)
                    .execute()
                    .body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(oEmbedDoc);
            if (rootNode.has("title")) {
                tiktokInfo.setTitle(rootNode.get("title").asText());
            }
            if (rootNode.has("thumbnail_url")) {
                tiktokInfo.setThumbnailUrl(rootNode.get("thumbnail_url").asText());
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to fetch data from tiktok");
        }

        return tiktokInfo;
    }
}
