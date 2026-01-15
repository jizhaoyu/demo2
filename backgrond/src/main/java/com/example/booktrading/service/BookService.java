package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.BookDTO;
import com.example.booktrading.entity.dto.BookQueryDTO;
import com.example.booktrading.entity.vo.BookVO;

/**
 * 书籍服务接口
 */
public interface BookService {
    
    /**
     * 发布书籍
     */
    Long create(BookDTO dto);
    
    /**
     * 编辑书籍
     */
    void update(Long id, BookDTO dto);
    
    /**
     * 删除书籍
     */
    void delete(Long id);
    
    /**
     * 更新书籍状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 获取书籍列表（分页、搜索、筛选）
     */
    IPage<BookVO> list(BookQueryDTO query);
    
    /**
     * 获取书籍详情
     */
    BookVO getById(Long id);
    
    /**
     * 获取我的书籍列表
     */
    IPage<BookVO> myBooks(Integer page, Integer size);
}
