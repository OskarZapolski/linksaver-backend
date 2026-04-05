package com.portfolio.linksaver.services;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Connection;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.TiktokVideoInfo;

@Service
public class TiktokVideoService {

    public TiktokVideoInfo handleTiktokVideo(NewLink newLink) {
        TiktokVideoInfo tiktokInfo = new TiktokVideoInfo();
        String oEmbedStart = "https://www.tiktok.com/oembed?url=";
        String oEmbedLink = oEmbedStart.concat(newLink.getUrl());
            
        try{
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
        }catch (IOException e) {
            throw new RuntimeException("failed to fetch data from tiktok");
        }
        
        return tiktokInfo;
    }
}
