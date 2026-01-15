package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.vo.BookVO;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    void add(Long bookId);
    
    /**
     * 取消收藏
     */
    void remove(Long bookId);
    
    /**
     * 获取收藏列表
     */
    IPage<BookVO> list(Integer page, Integer size);
    
    /**
     * 检查是否已收藏
     */
    boolean check(Long bookId);
}
