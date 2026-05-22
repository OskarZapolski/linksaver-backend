package com.portfolio.linksaver.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/get-category-videos")
    public ResponseEntity<List<VideoResponse>> extractVideos(@RequestParam("category") String category) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(videoService.extractUserVideos(category, user));
    }

    @GetMapping("/test")
    public String addTestLinks() {
        System.out.println("RADAR 1: Szukam użytkownika x@gmail.com...");

        // UWAGA: Zakładam, że masz w UserRepository metodę findByEmail!
        Optional<User> optionalUser = userRepository.findByEmail("x@gmail.com");

        if (optionalUser.isEmpty()) {
            return "BŁĄD: Nie znaleziono użytkownika x@gmail.com w bazie!";
        }

        User user = optionalUser.get();
        System.out.println("RADAR 2: Znaleziono użytkownika! ID: " + user.getUserId());

        // Tutaj wklej PRAWDZIWE linki z TikToka (jeśli dasz fake'owe, Twój scraper może
        // wyrzucić błąd)
        List<String> testUrls = Arrays.asList(
                "https://www.tiktok.com/@ewa.masterchef/video/7612346918861081878?is_from_webapp=1&sender_device=pc", // Tu
                                                                                                                      // podmień
                                                                                                                      // na
                                                                                                                      // swoje
                "https://www.tiktok.com/@waszek_mati/video/7618131117522111777?is_from_webapp=1&sender_device=pc", // Tu
                                                                                                                   // podmień
                                                                                                                   // na
                                                                                                                   // swoje
                "https://www.tiktok.com/@kizo_boss_mts/video/7620445316940156192?is_from_webapp=1&sender_device=pc",
                "https://youtube.com/shorts/_ij3o1RA8IY?si=nKX6tB_sSTdEL3Yo",
                "https://youtube.com/shorts/qRVqlI576FE?si=xDxMPRuuf_vHUL0i"// Tu podmień na swoje
        );

        // Kategoria (jeśli NewLink przyjmuje też kategorię, żeby przypisać do różnych)
        List<String> categories = Arrays.asList("Gry", "Muzyka", "Edukacja");

        System.out.println("RADAR 3: Zaczynam dodawać linki...");

        for (int i = 0; i < testUrls.size(); i++) {
            NewLink newLink = new NewLink();
            newLink.setUrl(testUrls.get(i));

            // Jeśli w Twoim DTO "NewLink" dodajesz od razu kategorię, odkomentuj to:
            // newLink.setCategory(categories.get(i));

            System.out.println("RADAR 4." + (i + 1) + ": Kucharz gotuje link " + (i + 1) + "...");

            try {
                videoService.processVideo(newLink, user);
                System.out.println(" Sukces dla linku " + (i + 1));
            } catch (Exception e) {
                System.out.println(" BŁĄD przy linku " + (i + 1) + ": " + e.getMessage());
            }
        }

        System.out.println("RADAR 5: Wszystkie linki przetworzone!");
        return "Zrobione! Odśwież teraz aplikację na telefonie i sprawdź HomeScreen!";
    }
}
