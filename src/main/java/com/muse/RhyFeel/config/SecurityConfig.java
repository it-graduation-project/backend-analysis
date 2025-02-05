package com.muse.RhyFeel.config;

import com.muse.RhyFeel.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (REST API용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용하므로 STATELESS 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용 (CORS Preflight 해결)
                        .requestMatchers("/auth/**").permitAll()  // 로그인, 회원가입은 인증 없이 허용
                        .requestMatchers("/").permitAll() // 루트 경로 허용
                        .requestMatchers("/analysis/**").authenticated()  // 음악 분석 API는 인증 필요
                        .requestMatchers("/files/**").authenticated()  // 파일 업로드/조회는 인증 필요
                        .anyRequest().authenticated() // 그 외 경로는 인증 필요
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //JWT 필터 적용

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 일단 모든 도메인 허용
        configuration.setAllowedOriginPatterns(List.of("*"));

        // 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 모든 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));

        // 클라이언트에서 Authorization 헤더 접근 가능하도록 설정
        configuration.setExposedHeaders(List.of("Authorization"));

        // Credentials 허용 (JWT 인증 사용 시 필요)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
