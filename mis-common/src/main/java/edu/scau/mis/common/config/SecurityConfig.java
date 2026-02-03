package edu.scau.mis.common.config;

import edu.scau.mis.common.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 开启注解权限控制
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 👈 关键：开启 @PreAuthorize 注解，让各模块能自己控制细粒度权限
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    // 把加密器也放这里，大家公用
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                 // 放行库存回滚接口 (仅供内部调用)
                 .requestMatchers("/product/unlockStock").permitAll()

                 // 列表接口：所有人可见
                 .requestMatchers( "/coupon/list").permitAll()

                // 1. 静态资源与文档 (所有模块通用)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                
                // 2. 认证相关 (主要服务于 mis-auth，其他模块放行也无所谓，因为没有对应接口)
                .requestMatchers("/auth/login", "/auth/register", "/auth/code", "/auth/logout").permitAll()
                
                // 3. 公共工具 (主要服务于 mis-web)
                .requestMatchers("/common/upload").permitAll()

                // 4. 其余所有请求必须认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(200);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\": 401, \"msg\": \"未授权，请登录\", \"data\": null}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(200);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\": 403, \"msg\": \"权限不足\", \"data\": null}");
                })
            );

        return http.build();
    }
}