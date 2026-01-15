package com.example.booktrading.interceptor;

import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.utils.JwtUtil;
import com.example.booktrading.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 允许匿名访问的路径（有Token则解析，无Token也放行）
    private static final List<String> ANONYMOUS_PATHS = Arrays.asList(
            "/book/list",
            "/book/[0-9]+",  // 书籍详情，使用正则匹配数字ID
            "/category/list",
            "/user/[0-9]+",  // 用户详情，使用正则匹配数字ID
            "/review/user/**",
            "/ai/chat"       // AI对话接口允许匿名访问
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI().replace("/api", "");
        
        // 获取Token
        String token = getToken(request);
        
        // 检查是否是允许匿名访问的路径
        boolean isAnonymousPath = isAnonymousPath(path);
        
        if (!StringUtils.hasText(token)) {
            if (isAnonymousPath) {
                // 匿名路径，无Token也放行
                return true;
            }
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            if (isAnonymousPath) {
                // 匿名路径，Token无效也放行（不设置用户上下文）
                return true;
            }
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 解析用户信息并存入上下文
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        Integer role = jwtUtil.getRole(token);
        
        UserContext.setUser(userId, username, role);
        
        return true;
    }
    
    /**
     * 检查是否是允许匿名访问的路径
     */
    private boolean isAnonymousPath(String path) {
        // 检查是否匹配 /book/{数字} 或 /user/{数字}
        if (path.matches("/book/\\d+") || path.matches("/user/\\d+")) {
            return true;
        }
        // 检查其他匿名路径
        for (String pattern : ANONYMOUS_PATHS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }
    
    /**
     * 从请求头获取Token
     */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
