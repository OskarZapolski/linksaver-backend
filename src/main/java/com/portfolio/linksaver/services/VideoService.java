package com.portfolio.linksaver.services;

import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.VideoScrapedData;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import com.portfolio.linksaver.repositories.VideoRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final AiPromptService aiPromptService;
    private final HtmlScraperService htmlScraperService;
 
    public VideoService(VideoRepository repository,
        AiPromptService aiPromptService, HtmlScraperService htmlScraperService) 
    {
        this.videoRepository = repository;
        this.aiPromptService = aiPromptService;
        this.htmlScraperService = htmlScraperService;
        
    }
    public List<Video> extractUserVideos(User user) {
        return videoRepository.findAllByUser(user);
    }

    public String processVideo(NewLink newLink, User user) {
        Video video = new Video();
        
        VideoScrapedData videoScrapedData = htmlScraperService.scrapeVideoData(newLink);
        System.out.println("   -> KUCHARZ 3: Scraper wrócił! Payload dla AI to: " + videoScrapedData.getAiPayload());
        String category = aiPromptService.promptCategory(videoScrapedData.getAiPayload());

        video.setImageUrl(videoScrapedData.getImageUrl());
        video.setCategory(category);
        video.setSaveDate(LocalDateTime.now());
        video.setUrl(newLink.getUrl());
        user.addVideo(video);
        System.out.println(category);
        
        videoRepository.save(video);
        System.out.println("   -> KUCHARZ 4: Wideo zapisane w bazie!");
        
        return "Url added";
    }

    
}
