package com.muse.RhyFeel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class RhyFeelApplication {
	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure()
				.directory(System.getProperty("user.dir")) // 현재 프로젝트 디렉토리에서 .env 찾기
				.ignoreIfMissing() // .env 파일이 없어도 예외 발생 X
				.load();

		// 필수 환경변수 로드 (널 체크)
		setSystemProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		setSystemProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		setSystemProperty("NGROK_URL", dotenv.get("NGROK_URL"));
		setSystemProperty("FILE_UPLOAD_DIR", dotenv.get("FILE_UPLOAD_DIR"));
		setSystemProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY")); // 🔥 JWT 키도 안전하게 로드

		// 스프링 애플리케이션 실행
		SpringApplication.run(RhyFeelApplication.class, args);
	}

	// ❗ 환경변수가 null이면 설정하지 않도록 예외 처리
	private static void setSystemProperty(String key, String value) {
		if (value != null && !value.trim().isEmpty()) {
			System.setProperty(key, value);
			System.out.println("✅ Loaded ENV: " + key);
		} else {
			System.out.println("⚠️ Missing ENV: " + key);
		}
	}
}
