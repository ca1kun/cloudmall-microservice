package edu.scau.mis.api.config; // 推荐放在 web 模块的 config 包下

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // addMapping("/**") 表示对所有API接口生效
        registry.addMapping("/**")
                // allowedOriginPatterns("*") 表示允许所有来源的域名访问
                // 在生产环境中，您应该替换为您的前端域名，例如 .allowedOrigins("http://your-frontend.com")
                .allowedOriginPatterns("*")
                // allowedMethods("*") 表示允许所有请求方法 (GET, POST, PUT, DELETE, etc.)
                .allowedMethods("*")
                // allowCredentials(true) 表示允许请求携带凭证（如 Cookies）
                .allowCredentials(true)
                // maxAge(3600) 表示预检请求（OPTIONS请求）的有效期，单位为秒
                .maxAge(3600);
    }
}