package com.muse.RhyFeel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/upload").permitAll() // 업로드 API는 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .httpBasic(withDefaults()); // 기본 HTTP 인증 방식 사용

        return http.build();
    }
}

