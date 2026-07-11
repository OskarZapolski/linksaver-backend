package com.portfolio.linksaver.controllers;

import com.portfolio.linksaver.repositories.VideoRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;

import com.portfolio.linksaver.dto.CategoryResponse;
import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.dto.VideoResponse;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import com.portfolio.linksaver.repositories.UserRepository;
import com.portfolio.linksaver.services.VideoService;

import jakarta.validation.Valid;

@RestController
public class VideoController {

    private final VideoRepository videoRepository;
    private VideoService videoService;
    private UserRepository userRepository;

    public VideoController(VideoService videoService, UserRepository userRepository, VideoRepository videoRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;

    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId, @AuthenticationPrincipal User user) {
        Video video = videoRepository.findByVideoIdAndUser(videoId, user)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono filmu lub brak dostępu"));

        videoRepository.delete(video);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/video")
    public String getLink(@Valid @RequestBody NewLink videoLink) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        videoService.processVideo(videoLink, user);
        return "link dotarl";
    }

    @GetMapping("/videos-information")
    public ResponseEntity<List<CategoryResponse>> extractInformation() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(videoService.extractVideoInformation(user));

    }

    @GetMapping("/get-category-videos")
    public ResponseEntity<Page<VideoResponse>> extractVideos(@RequestParam("category") String category,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("saveDate").descending());

        Page<VideoResponse> videoPage = videoRepository.findVideosByCategory(category, user, pageable);
        return ResponseEntity.ok(videoPage);
    }

}
