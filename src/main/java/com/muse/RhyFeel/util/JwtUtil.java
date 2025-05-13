package com.muse.RhyFeel.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY_STRING;
    private static final Key SECRET_KEY;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24시간 (밀리초)

    static {
        Dotenv dotenv = Dotenv.load();
        SECRET_KEY_STRING = dotenv.get("JWT_SECRET_KEY");

        if (SECRET_KEY_STRING == null || SECRET_KEY_STRING.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY 환경변수가 설정되지 않았습니다!");
        }

        // 공백 제거 후 길이 체크
        String cleanedKey = SECRET_KEY_STRING.replaceAll("\\s+", ""); // 공백 제거
        System.out.println("Cleaned SECRET_KEY: " + cleanedKey);

        try {
            if (cleanedKey.matches("^[A-Za-z0-9+/=]+$") && cleanedKey.length() % 4 == 0) {
                System.out.println("SECRET_KEY is Base64 encoded. Decoding...");
                SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(cleanedKey));
            } else {
                System.out.println("SECRET_KEY is plaintext. Using as is.");
                SECRET_KEY = Keys.hmacShaKeyFor(cleanedKey.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("SECRET_KEY 초기화 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JWT 생성
     */
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT에서 이메일 추출
     */
    public static String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 유효성 검사
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }
}
