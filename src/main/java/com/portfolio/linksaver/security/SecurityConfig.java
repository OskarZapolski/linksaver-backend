package com.portfolio.linksaver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.portfolio.linksaver.services.OAuth2Service;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2Service oAuth2Service;
    private final OAuth2LogInSuccessHandler oAuth2LogInSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(OAuth2Service oAuth2Service, OAuth2LogInSuccessHandler oAuth2LogInSuccessHandler, 
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.oAuth2Service = oAuth2Service;
        this.oAuth2LogInSuccessHandler = oAuth2LogInSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/test", "/register", "/log-in", "/auth/refresh-token", "/error").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oAuth2 -> oAuth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(oAuth2Service)
            ).successHandler(oAuth2LogInSuccessHandler)
        );
        // .rememberMe(remember -> remember
        //     .key("superTajnyKluczDoSzyfrowaniaCiasteczek123")
        //     .tokenValiditySeconds(2592000)
        // );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
