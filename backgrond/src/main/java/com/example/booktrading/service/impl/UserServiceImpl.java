package com.example.booktrading.service.impl;

import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.PasswordUpdateDTO;
import com.example.booktrading.entity.dto.UserUpdateDTO;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.UserVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.UserService;
import com.example.booktrading.utils.PasswordUtil;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    
    @Override
    public UserVO getProfile() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    @Override
    public void updateProfile(UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 更新字段
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        
        userMapper.updateById(user);
    }
    
    @Override
    public void updatePassword(PasswordUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!passwordUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordUtil.encode(dto.getNewPassword()));
        userMapper.updateById(user);
    }
    
    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
