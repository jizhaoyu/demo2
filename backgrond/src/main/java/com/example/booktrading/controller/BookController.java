package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.BookDTO;
import com.example.booktrading.entity.dto.BookQueryDTO;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 书籍控制器
 */
@Tag(name = "书籍管理", description = "书籍发布、编辑、搜索等")
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    @Operation(summary = "获取书籍列表")
    @GetMapping("/list")
    public Result<IPage<BookVO>> list(BookQueryDTO query) {
        IPage<BookVO> page = bookService.list(query);
        return Result.success(page);
    }
    
    @Operation(summary = "获取书籍详情")
    @GetMapping("/{id}")
    public Result<BookVO> getById(@PathVariable Long id) {
        BookVO bookVO = bookService.getById(id);
        return Result.success(bookVO);
    }
    
    @Operation(summary = "发布书籍")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody BookDTO dto) {
        Long id = bookService.create(dto);
        return Result.success(id);
    }
    
    @Operation(summary = "编辑书籍")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        bookService.update(id, dto);
        return Result.success();
    }
    
    @Operation(summary = "删除书籍")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success();
    }
    
    @Operation(summary = "更新书籍状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "获取我的书籍列表")
    @GetMapping("/my")
    public Result<IPage<BookVO>> myBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<BookVO> pageResult = bookService.myBooks(page, size);
        return Result.success(pageResult);
    }
}
