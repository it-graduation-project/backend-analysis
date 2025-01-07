package com.muse.RhyFeel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class RhyFeelApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory(System.getProperty("user.dir")) // 현재 프로젝트 디렉토리에서 .env 찾기
				.load();
		System.setProperty("SPRING_SECURITY_PASSWORD", dotenv.get("SPRING_SECURITY_PASSWORD"));
		System.setProperty("SPRING_SECURITY_USER", dotenv.get("SPRING_SECURITY_USER"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("NGROK_URL", dotenv.get("NGROK_URL"));
		System.setProperty("FILE_UPLOAD_DIR", dotenv.get("FILE_UPLOAD_DIR"));
		SpringApplication.run(RhyFeelApplication.class, args);
	}
}

