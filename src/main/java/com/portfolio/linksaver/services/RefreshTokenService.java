package com.portfolio.linksaver.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.repositories.UserRepository;

@Service
public class RefreshTokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public RefreshTokenService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    public User findUserByToken(String token) {
        String email = jwtService.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("token is not valid");
        }
        return user.get();
    }

    public String generateNewAccessToken(String refreshToken, User user) {
        if (jwtService.isTokenExpired(refreshToken) || !jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Token is not valid or is expired");
        }
        return jwtService.generateAccessToken(user);

    }
}
