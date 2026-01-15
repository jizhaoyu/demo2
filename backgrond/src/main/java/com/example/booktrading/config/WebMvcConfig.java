package com.example.booktrading.config;

import com.example.booktrading.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final JwtInterceptor jwtInterceptor;
    
    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                // 排除公开接口（完全不经过拦截器）
                .excludePathPatterns(
                        // 认证接口
                        "/auth/register",
                        "/auth/login",
                        "/auth/hash",
                        // Swagger文档
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/doc.html",
                        // 静态资源
                        "/favicon.ico",
                        "/error"
                );
        // 注意：/book/list, /book/{id}, /category/list, /user/{id} 等公开接口
        // 在 JwtInterceptor 中通过 isAnonymousPath 方法处理，允许匿名访问
    }
}
