package edu.scau.mis.cart.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    /**
     * Feign 请求拦截器
     * 作用：在 Feign 发起请求之前，把当前请求头里的 Authorization (Token) 拿出来，
     *       塞到 Feign 的请求头里，传给下游服务。
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1. 获取当前请求的上下文 (也就是前端发给 mis-cart 的那个请求)
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    
                    // 2. 获取 Token
                    String token = request.getHeader("Authorization");
                    
                    // 3. 如果 Token 存在，就把它传递给 Feign 请求
                    if (token != null) {
                        // 这里的 Key 必须和后端过滤器检查的 Key 一致
                        template.header("Authorization", token);
                        System.out.println("🚀 Feign 已自动携带 Token 进行远程调用...");
                    }
                }
            }
        };
    }
}