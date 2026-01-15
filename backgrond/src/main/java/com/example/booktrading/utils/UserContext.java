package com.example.booktrading.utils;

import lombok.Data;

/**
 * 用户上下文 - 存储当前登录用户信息
 */
public class UserContext {
    
    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前用户
     */
    public static void setUser(Long userId, String username, Integer role) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUsername(username);
        userInfo.setRole(role);
        USER_HOLDER.set(userInfo);
    }
    
    /**
     * 获取当前用户
     */
    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }
    
    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }
    
    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null && userInfo.getRole() == 1;
    }
    
    /**
     * 清除当前用户
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
    
    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private Integer role;
    }
}
