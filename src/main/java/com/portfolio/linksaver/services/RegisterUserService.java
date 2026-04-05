package com.portfolio.linksaver.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portfolio.linksaver.dto.NewUserData;
import com.portfolio.linksaver.dto.Tokens;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.enums.AuthProvider;
import com.portfolio.linksaver.repositories.UserRepository;

@Service
public class RegisterUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public RegisterUserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        JwtService jwtService) 
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Tokens handleNewUser(NewUserData newUserData) {
        Optional<User> optionalUser = userRepository.findByEmail(newUserData.getEmail());
        if (optionalUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(newUserData.getEmail());
            newUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));
            newUser.setUserName(newUserData.getUserName());
            newUser.setAuthProvider(AuthProvider.LOCAL);
            userRepository.save(newUser);   

            String accessToken = jwtService.generateAccessToken(newUser);
            String refreshToken = jwtService.generateRefreshToken(newUser);
            return new Tokens(accessToken, refreshToken);
        } else {
            throw new IllegalArgumentException("email is already used");
        }
    }
}
