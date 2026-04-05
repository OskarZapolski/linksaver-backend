package com.portfolio.linksaver.services;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.portfolio.linksaver.entities.User;
import com.portfolio.linksaver.enums.AuthProvider;
import com.portfolio.linksaver.repositories.UserRepository;

@Service
public class OAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
        
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2user = super.loadUser(userRequest);

        String email = oAuth2user.getAttribute("email");
        String name = oAuth2user.getAttribute("name");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = new User();
        if (!optionalUser.isEmpty()) {
            user = optionalUser.get();
            user.setUserName(name);
            
            userRepository.save(user);
        }else {
            
            user.setEmail(email);
            user.setUserName(name);
            user.setAuthProvider(AuthProvider.GOOGLE);
            userRepository.save(user);
        }
        
        return oAuth2user;
    }
}
