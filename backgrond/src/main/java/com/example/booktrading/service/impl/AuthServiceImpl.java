package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.LoginDTO;
import com.example.booktrading.entity.dto.RegisterDTO;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.LoginVO;
import com.example.booktrading.entity.vo.UserVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.AuthService;
import com.example.booktrading.utils.JwtUtil;
import com.example.booktrading.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    
    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS, "用户名已存在");
        }
        
        // 检查邮箱是否存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, dto.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS, "邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordUtil.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(0); // 普通用户
        user.setStatus(1); // 正常状态
        
        userMapper.insert(user);
    }
    
    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证密码
        if (!passwordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUser(userVO);
        
        return loginVO;
    }
    
    @Override
    public void logout() {
        // JWT是无状态的，客户端删除Token即可
        // 如需实现Token黑名单，可在此处添加逻辑
    }
}
