package com.portfolio.linksaver.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.linksaver.dto.Tokens;
import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.repositories.UserRepository;
import com.portfolio.linksaver.services.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LogInSuccessHandler implements AuthenticationSuccessHandler {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2LogInSuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
        Authentication authentication) throws IOException, ServletException
    {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).get();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Tokens tokens = new Tokens(accessToken, refreshToken);

        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        httpResponse.getWriter().write(objectMapper.writeValueAsString(tokens));

        // httpResponse.sendRedirect("twoja-apka://login-sukces?access=" + accessToken + "&refresh=" + refreshToken);
    }
}
