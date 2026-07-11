package com.portfolio.linksaver.services;

import com.portfolio.linksaver.dto.CategoryResponse;
import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.VideoScrapedData;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import com.portfolio.linksaver.repositories.UserRepository;
import com.portfolio.linksaver.repositories.VideoRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final AiPromptService aiPromptService;
    private final HtmlScraperService htmlScraperService;
    private final UserRepository userRepository;

    public VideoService(VideoRepository repository,
            AiPromptService aiPromptService, HtmlScraperService htmlScraperService, UserRepository userRepository) {
        this.videoRepository = repository;
        this.aiPromptService = aiPromptService;
        this.htmlScraperService = htmlScraperService;
        this.userRepository = userRepository;

    }

    public List<CategoryResponse> extractVideoInformation(User user) {
        List<CategoryResponse> listCategoryResponses = new ArrayList<>();
        List<Object[]> categoryStats = videoRepository.findCategoryStatsByUserId(user.getUserId());

        for (Object[] stats : categoryStats) {

            String category = (String) stats[0];
            Long count = (Long) stats[1];
            String thumbnailImage = (String) stats[2];
            LocalDateTime saveDate = (LocalDateTime) stats[3];

            CategoryResponse categoryResponse = new CategoryResponse(category, count, thumbnailImage, saveDate);
            listCategoryResponses.add(categoryResponse);
        }

        return listCategoryResponses;
    }

    @Transactional
    public String processVideo(NewLink newLink, User userFromController) {
        Video video = new Video();

        User user = userRepository.findById(userFromController.getUserId())
                .orElseThrow(() -> new RuntimeException("nie znaleziono uzytkownika"));

        VideoScrapedData videoScrapedData = htmlScraperService.scrapeVideoData(newLink);
        String category = aiPromptService.promptCategory(videoScrapedData.getAiPayload());

        video.setImageUrl(videoScrapedData.getImageUrl());
        video.setCategory(category);
        video.setSaveDate(LocalDateTime.now());
        video.setUrl(newLink.getUrl());
        video.setUser(user);
        user.addVideo(video);

        videoRepository.save(video);

        return "Url added";
    }

}
