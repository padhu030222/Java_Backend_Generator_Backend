package com.pjds.backend_generator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()  // ✅ allow cross-origin requests via CorsConfig bean
            .and()
            .csrf().disable() // ✅ disable CSRF for APIs (not forms)
            .authorizeHttpRequests()
            .anyRequest().permitAll(); // ✅ open API to all requests

        return http.build();
    }
}
