package com.example.booktrading.service;

import com.example.booktrading.entity.dto.LoginDTO;
import com.example.booktrading.entity.dto.RegisterDTO;
import com.example.booktrading.entity.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 用户登出
     */
    void logout();
}
