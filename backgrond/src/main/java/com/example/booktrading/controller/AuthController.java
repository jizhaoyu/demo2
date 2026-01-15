package com.example.booktrading.controller;

import com.example.booktrading.entity.dto.LoginDTO;
import com.example.booktrading.entity.dto.RegisterDTO;
import com.example.booktrading.entity.vo.LoginVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.AuthService;
import com.example.booktrading.utils.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户注册、登录、登出")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final PasswordUtil passwordUtil;
    
    /**
     * 临时接口：生成密码哈希值（用完后删除）
     */
    @Operation(summary = "生成密码哈希")
    @GetMapping("/hash")
    public Result<Map<String, String>> generateHash(@RequestParam String password) {
        String hash = passwordUtil.encode(password);
        return Result.success(Map.of("password", password, "hash", hash));
    }
    
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }
    
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success(loginVO);
    }
    
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
