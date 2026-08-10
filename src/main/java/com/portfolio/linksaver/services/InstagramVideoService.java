package com.portfolio.linksaver.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.ScrapedVideoInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InstagramVideoService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String RAPID_API_KEY;
    private final String RAPID_API_HOST;

    private final Cloudinary cloudinary;

    public InstagramVideoService(
            @Value("${rapidapi.key}") String rapidApiKey,
            @Value("${rapidapi.host}") String rapidApiHost,
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.RAPID_API_KEY = rapidApiKey;
        this.RAPID_API_HOST = rapidApiHost;

        // Inicjalizujemy Cloudinary bezpiecznie przekazanymi kluczami
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public ScrapedVideoInfo handleInstagramVideo(NewLink newLink) {
        ScrapedVideoInfo scrapedVideoInfo = new ScrapedVideoInfo();
        String url = newLink.getUrl();

        try {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }

            String apiUrl = "https://" + RAPID_API_HOST + "/post?url={igUrl}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", RAPID_API_KEY);
            headers.set("X-RapidAPI-Host", RAPID_API_HOST);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class, url);
            JsonNode rootNode = mapper.readTree(response.getBody());

            JsonNode dataNode = rootNode;
            String captionText = "";
            if (dataNode.has("accessibility_caption")) {
                captionText = dataNode.get("accessibility_caption").asText();
            }

            String thumbnailUrl = "";
            if (dataNode.has("thumbnail_src")) {
                thumbnailUrl = dataNode.get("thumbnail_src").asText();

            }
            String permamentThumbnailUrl = thumbnailUrl;
            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                try {

                    Map<String, Object> uploadResults = cloudinary.uploader().upload(thumbnailUrl,
                            ObjectUtils.emptyMap());
                    permamentThumbnailUrl = uploadResults.get("secure_url").toString();
                } catch (Exception ex) {
                    System.err.println("Błąd wrzucania na Cloudinary: " + ex.getMessage());
                }
            }

            scrapedVideoInfo.setThumbnailUrl(permamentThumbnailUrl);
            scrapedVideoInfo.setTitle(captionText);
        } catch (Exception e) {
            System.err.println("Błąd pobierania danych z IG (RapidAPI): " + e.getMessage());
            scrapedVideoInfo.setThumbnailUrl("https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png");
            scrapedVideoInfo.setTitle("Film z Instagrama. Kategoryzuj na podstawie linku: " + url);
        }

        return scrapedVideoInfo;
    }

}