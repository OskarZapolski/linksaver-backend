package com.portfolio.linksaver.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.portfolio.linksaver.dto.LogInUserData;
import com.portfolio.linksaver.dto.NewUserData;
import com.portfolio.linksaver.dto.Tokens;
import com.portfolio.linksaver.dto.oAuth2Token;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.services.LogInUserService;
import com.portfolio.linksaver.services.OAuth2Service;
import com.portfolio.linksaver.services.RefreshTokenService;
import com.portfolio.linksaver.services.RegisterUserService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {
    private final RegisterUserService registerUserService;
    private final LogInUserService logInUserService;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2Service oAuth2Service;

    public UserController(RegisterUserService registerUserService, LogInUserService logInUserService,
            RefreshTokenService refreshTokenService, OAuth2Service oAuth2Service) {
        this.registerUserService = registerUserService;
        this.logInUserService = logInUserService;
        this.refreshTokenService = refreshTokenService;
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, String>> checkAuth(@AuthenticationPrincipal User user) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", user.getUserName());
        userInfo.put("email", user.getEmail());
        userInfo.put("picture", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/register")
    public ResponseEntity<Tokens> registerUser(@Valid @RequestBody NewUserData newUserData) {
        Tokens tokens = registerUserService.handleNewUser(newUserData);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/log-in")
    public ResponseEntity<Tokens> logInUser(@Valid @RequestBody LogInUserData logInUserData) {
        Tokens tokens = logInUserService.handleLogInUser(logInUserData);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/oAuth2")
    public ResponseEntity<Tokens> googleLogin(@RequestBody oAuth2Token token) {
        Tokens tokens = oAuth2Service.authenticateWithGoogle(token.getToken());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<Map<String, String>> handleRefreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        User user = refreshTokenService.findUserByToken(refreshToken);
        System.out.print("have user");
        String newAccessToken = refreshTokenService.generateNewAccessToken(refreshToken, user);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

}
