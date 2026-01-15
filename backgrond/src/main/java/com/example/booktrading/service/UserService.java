package com.example.booktrading.service;

import com.example.booktrading.entity.dto.PasswordUpdateDTO;
import com.example.booktrading.entity.dto.UserUpdateDTO;
import com.example.booktrading.entity.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取当前用户信息
     */
    UserVO getProfile();
    
    /**
     * 更新个人信息
     */
    void updateProfile(UserUpdateDTO dto);
    
    /**
     * 修改密码
     */
    void updatePassword(PasswordUpdateDTO dto);
    
    /**
     * 获取用户公开信息
     */
    UserVO getUserById(Long id);
}
