package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.ReviewDTO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.entity.vo.ReviewVO;
import com.example.booktrading.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 */
@Tag(name = "评价管理", description = "交易评价功能")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Operation(summary = "提交评价")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ReviewDTO dto) {
        reviewService.create(dto);
        return Result.success();
    }
    
    @Operation(summary = "获取用户收到的评价列表")
    @GetMapping("/user/{userId}")
    public Result<IPage<ReviewVO>> listByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<ReviewVO> pageResult = reviewService.listByUserId(userId, page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取订单的评价")
    @GetMapping("/order/{orderId}")
    public Result<List<ReviewVO>> listByOrderId(@PathVariable Long orderId) {
        List<ReviewVO> reviews = reviewService.listByOrderId(orderId);
        return Result.success(reviews);
    }
}
