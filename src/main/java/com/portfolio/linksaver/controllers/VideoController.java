package com.portfolio.linksaver.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;

import com.portfolio.linksaver.dto.CategoryResponse;
import com.portfolio.linksaver.dto.NewLink;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.entities.Video;
import com.portfolio.linksaver.repositories.UserRepository;
import com.portfolio.linksaver.services.VideoService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class VideoController {

    private VideoService videoService;
    private UserRepository userRepository;

    public VideoController(VideoService videoService, UserRepository userRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;

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

    @GetMapping("/get-videos")
    public ResponseEntity<List<Video>> extractVideos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(videoService.extractUserVideos(user));
    }

    @GetMapping("/test")
    public String tester() {
        System.out.println("RADAR 1: Wszedłem do kontrolera!");
        NewLink newLink = new NewLink();
        User user = new User();

        user.setUserName("testowy");
        userRepository.save(user);
        System.out.println("RADAR 3: User zapisany pomyślnie! ID: " + user.getUserId());
        newLink.setUrl("https://www.instagram.com/reel/DV5LLJEjmcU/?utm_source=ig_web_copy_link&igsh=NTc4MTIwNjQ2YQ==");
        System.out.println("RADAR 4: Wywołuję Kucharza (VideoService)...");
        videoService.processVideo(newLink, user);
        System.out.println("RADAR 5: Kucharz skończył pracę!");
        return "zrobione";
    }
}
