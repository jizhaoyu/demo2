package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.entity.vo.UserVO;
import com.example.booktrading.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 */
@Tag(name = "管理员功能", description = "系统管理功能")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @Operation(summary = "获取用户列表")
    @GetMapping("/users")
    public Result<IPage<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<UserVO> pageResult = adminService.getUserList(page, size, keyword, status);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "更新用户状态")
    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "获取系统统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = adminService.getStatistics();
        return Result.success(stats);
    }
    
    @Operation(summary = "获取书籍列表（管理员）")
    @GetMapping("/books")
    public Result<IPage<BookVO>> getBookList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<BookVO> pageResult = adminService.getBookList(page, size, keyword, status);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "删除书籍（管理员）")
    @DeleteMapping("/book/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        adminService.deleteBook(id);
        return Result.success();
    }
}
