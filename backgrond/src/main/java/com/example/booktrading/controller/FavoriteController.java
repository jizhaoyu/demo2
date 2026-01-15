package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Tag(name = "收藏管理", description = "书籍收藏功能")
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    @Operation(summary = "获取收藏列表")
    @GetMapping("/list")
    public Result<IPage<BookVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<BookVO> pageResult = favoriteService.list(page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "添加收藏")
    @PostMapping("/{bookId}")
    public Result<Void> add(@PathVariable Long bookId) {
        favoriteService.add(bookId);
        return Result.success();
    }
    
    @Operation(summary = "取消收藏")
    @DeleteMapping("/{bookId}")
    public Result<Void> remove(@PathVariable Long bookId) {
        favoriteService.remove(bookId);
        return Result.success();
    }
    
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{bookId}")
    public Result<Boolean> check(@PathVariable Long bookId) {
        boolean isFavorited = favoriteService.check(bookId);
        return Result.success(isFavorited);
    }
}
