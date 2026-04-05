package com.portfolio.linksaver.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.portfolio.linksaver.dto.LogInUserData;
import com.portfolio.linksaver.dto.Tokens;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.repositories.UserRepository;

@Service
public class LogInUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LogInUserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        JwtService jwtService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Tokens handleLogInUser(LogInUserData logInUserData) {
        Optional<User> optionalUser = userRepository.findByEmail(logInUserData.getEmail());

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("wrong email or password");
        }

        User dataBaseUser = optionalUser.get();
        if (!passwordEncoder.matches(logInUserData.getPassword(), dataBaseUser.getPassword())) {
            throw new IllegalArgumentException("wrong email or password");
        }

        String accessToken = jwtService.generateAccessToken(dataBaseUser);
        String refreshToken = jwtService.generateRefreshToken(dataBaseUser);

        return new Tokens(accessToken, refreshToken);
        
    }
}
