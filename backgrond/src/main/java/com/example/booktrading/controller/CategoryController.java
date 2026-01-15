package com.example.booktrading.controller;

import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.vo.CategoryVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.service.CategoryService;
import com.example.booktrading.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Tag(name = "分类管理", description = "书籍分类管理")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(summary = "获取分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
    
    @Operation(summary = "获取分类列表（包含书籍数量，管理员用）")
    @GetMapping("/list/admin")
    public Result<List<CategoryVO>> listWithBookCount() {
        checkAdmin();
        List<CategoryVO> categories = categoryService.listWithBookCount();
        return Result.success(categories);
    }
    
    @Operation(summary = "创建分类（管理员）")
    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        checkAdmin();
        categoryService.create(category);
        return Result.success();
    }
    
    @Operation(summary = "更新分类（管理员）")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        checkAdmin();
        categoryService.update(id, category);
        return Result.success();
    }
    
    @Operation(summary = "删除分类（管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        categoryService.delete(id);
        return Result.success();
    }
    
    /**
     * 检查是否为管理员
     */
    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
