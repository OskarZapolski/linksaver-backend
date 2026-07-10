package com.portfolio.linksaver.services;

import java.util.Collections;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import com.portfolio.linksaver.dto.Tokens;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.enums.AuthProvider;
import com.portfolio.linksaver.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${google.client.id}")
    private String googleClientId;

    public OAuth2Service(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }

    @Transactional
    public Tokens authenticateWithGoogle(String token) {
        try {
            GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = googleIdTokenVerifier.verify(token);

            if (idToken != null) {
                User user = handleGoogleUser(idToken);

                String accessToken = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                return new Tokens(accessToken, refreshToken);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid google Id token");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token verification failed", e);

        }
    }

    private User handleGoogleUser(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        String pictureUrl = (String) payload.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isEmpty()) {
            User user = userOptional.get();
            user.setUserName(name);
            user.setAvatarUrl(pictureUrl);
            return user;
        } else {
            User user = new User();
            user.setEmail(email);
            user.setUserName(name);
            user.setAvatarUrl(pictureUrl);
            user.setAuthProvider(AuthProvider.GOOGLE);
            userRepository.save(user);
            return user;
        }
    }

}
