package com.muse.RhyFeel.filter;

import com.muse.RhyFeel.util.JwtUtil;
import com.muse.RhyFeel.model.User;
import com.muse.RhyFeel.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ JWT 없음 또는 잘못된 형식: " + authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String email = JwtUtil.extractEmail(token);
            System.out.println("✅ JWT 인증 성공 - 이메일: " + email);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                System.out.println("❌ JWT에 있는 이메일이 DB에 없음: " + email);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("User not found");
                return;
            }

            // 🔥 인증 정보 SecurityContext에 설정
            User user = userOptional.get();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("✅ JWT 검증 완료 - 사용자 확인됨: " + email);
        } catch (Exception e) {
            System.out.println("❌ JWT 검증 실패: " + e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
