package com.muse.RhyFeel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public WebConfig() {
        System.out.println("🟢 WebConfig - 일반 설정 적용 완료");
    }
}
