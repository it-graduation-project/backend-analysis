package com.muse.RhyFeel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Value("${ngrok.url}")
    private String ngrokUrl; // application.properties에서 값 읽기

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/").permitAll() // 루트 경로 허용
//                        .requestMatchers("/upload").permitAll() // 업로드 경로 허용
//                        .requestMatchers("/files").permitAll() // 업로드 경로 허용 (경로바꾼 것)
//                        .requestMatchers("/analysis/**").permitAll() // analysis 경로 허용
//                        .anyRequest().authenticated() // 그 외 경로는 인증 필요
                        .anyRequest().permitAll() // 모든 요청 허용 -> 개발 단계에서 사용 중
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", // 로컬 React 개발 서버
                ngrokUrl // application.properties 참조
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 등 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 엔드포인트에 적용
        return source;
    }
}
