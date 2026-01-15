package com.example.booktrading.controller;

import com.example.booktrading.entity.dto.PasswordUpdateDTO;
import com.example.booktrading.entity.dto.UserUpdateDTO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.entity.vo.UserVO;
import com.example.booktrading.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户信息管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        UserVO userVO = userService.getProfile();
        return Result.success(userVO);
    }
    
    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        userService.updateProfile(dto);
        return Result.success();
    }
    
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        userService.updatePassword(dto);
        return Result.success();
    }
    
    @Operation(summary = "获取用户公开信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }
}
